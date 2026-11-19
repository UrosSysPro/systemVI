"""Low-level mesh construction, boolean and validation helpers.

Everything in this module is independent of keyboard layouts.  It wraps the
``bmesh`` and depsgraph operations that are otherwise easy to get wrong in a
headless Blender run.
"""

from __future__ import annotations

import math
import sys
from dataclasses import dataclass

import bmesh
import bpy
from mathutils import Matrix, Vector

CYLINDER_SEGMENTS = 24
ZERO_AREA_EPSILON = 1e-9


def add_box(bm, centre, size, rotation=0.0):
    """Append an axis-aligned (then Z rotated) box to ``bm``."""

    ret = bmesh.ops.create_cube(bm, size=1.0)
    verts = ret["verts"]
    matrix = (
        Matrix.Translation(Vector(centre))
        @ Matrix.Rotation(rotation, 4, "Z")
        @ Matrix.Diagonal(Vector((size[0], size[1], size[2], 1.0)))
    )
    bmesh.ops.transform(bm, matrix=matrix, verts=verts)


def add_cylinder(bm, centre_xy, radius, z_min, z_max, segments=CYLINDER_SEGMENTS):
    """Append a closed vertical cylinder to ``bm``."""

    cx, cy = centre_xy
    bottom = []
    top = []
    for i in range(segments):
        angle = 2.0 * math.pi * i / segments
        x = cx + radius * math.cos(angle)
        y = cy + radius * math.sin(angle)
        bottom.append(bm.verts.new((x, y, z_min)))
        top.append(bm.verts.new((x, y, z_max)))

    bm.faces.new(tuple(reversed(bottom)))
    bm.faces.new(tuple(top))
    for i in range(segments):
        j = (i + 1) % segments
        bm.faces.new((bottom[i], bottom[j], top[j], top[i]))


def add_cylinder_between(bm, start, end, radius, segments=CYLINDER_SEGMENTS):
    """Append a closed cylinder between two 3D points."""

    start = Vector(start)
    end = Vector(end)
    direction = end - start
    length = direction.length
    if length <= 0.0:
        return

    ret = bmesh.ops.create_cone(
        bm,
        cap_ends=True,
        cap_tris=False,
        segments=segments,
        radius1=radius,
        radius2=radius,
        depth=length,
    )
    matrix = (
        Matrix.Translation((start + end) / 2.0)
        @ direction.to_track_quat("Z", "Y").to_matrix().to_4x4()
    )
    bmesh.ops.transform(bm, matrix=matrix, verts=ret["verts"])


def add_prism_x(bm, profile_yz, x0, x1):
    """Extrude a closed Y/Z profile along X into a solid."""

    ring0 = [bm.verts.new((x0, y, z)) for y, z in profile_yz]
    ring1 = [bm.verts.new((x1, y, z)) for y, z in profile_yz]

    bm.faces.new(tuple(ring0))
    bm.faces.new(tuple(reversed(ring1)))

    count = len(profile_yz)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((ring0[i], ring0[j], ring1[j], ring1[i]))


def add_prism_y(bm, profile_xz, y0, y1):
    """Extrude a closed X/Z profile along Y into a solid."""

    ring0 = [bm.verts.new((x, y0, z)) for x, z in profile_xz]
    ring1 = [bm.verts.new((x, y1, z)) for x, z in profile_xz]

    bm.faces.new(tuple(ring0))
    bm.faces.new(tuple(reversed(ring1)))

    count = len(profile_xz)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((ring0[i], ring0[j], ring1[j], ring1[i]))


def add_prism_z(bm, profile_xy, z0, z1):
    """Extrude a closed X/Y profile along Z into a solid."""

    ring0 = [bm.verts.new((x, y, z0)) for x, y in profile_xy]
    ring1 = [bm.verts.new((x, y, z1)) for x, y in profile_xy]

    bm.faces.new(tuple(ring0))
    bm.faces.new(tuple(reversed(ring1)))

    count = len(profile_xy)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((ring0[i], ring0[j], ring1[j], ring1[i]))


def mesh_object(name, bm):
    """Turn a bmesh into a linked scene object with outward facing normals."""

    bmesh.ops.recalc_face_normals(bm, faces=bm.faces)
    mesh = bpy.data.meshes.new(name)
    bm.to_mesh(mesh)
    bm.free()
    obj = bpy.data.objects.new(name, mesh)
    bpy.context.scene.collection.objects.link(obj)
    return obj


def apply_modifiers(obj):
    """Replace ``obj`` data by its evaluated (modifier applied) mesh.

    Avoids ``bpy.ops.object.modifier_apply`` and its context requirements, so
    this also works reliably in ``blender --background``.
    """

    if not obj.modifiers:
        return

    bpy.context.view_layer.update()
    depsgraph = bpy.context.evaluated_depsgraph_get()
    evaluated = obj.evaluated_get(depsgraph)
    new_mesh = bpy.data.meshes.new_from_object(evaluated)

    old_mesh = obj.data
    mesh_name = old_mesh.name
    obj.modifiers.clear()
    obj.data = new_mesh
    bpy.data.meshes.remove(old_mesh)
    new_mesh.name = mesh_name


def difference(target, cutter):
    """Subtract ``cutter`` from ``target`` and delete the cutter."""

    modifier = target.modifiers.new("cut", "BOOLEAN")
    modifier.operation = "DIFFERENCE"
    modifier.solver = "EXACT"
    modifier.object = cutter
    apply_modifiers(target)

    cutter_mesh = cutter.data
    bpy.data.objects.remove(cutter, do_unlink=True)
    bpy.data.meshes.remove(cutter_mesh)


def union(target, other):
    """Union ``other`` into ``target`` and delete ``other``."""

    modifier = target.modifiers.new("union", "BOOLEAN")
    modifier.operation = "UNION"
    modifier.solver = "EXACT"
    modifier.object = other
    apply_modifiers(target)

    other_mesh = other.data
    bpy.data.objects.remove(other, do_unlink=True)
    bpy.data.meshes.remove(other_mesh)


@dataclass(frozen=True)
class MeshReport:
    """Result of :func:`check_mesh`."""

    faces: int
    non_manifold_edges: int
    zero_area_faces: int

    @property
    def ok(self):
        return self.faces > 0 and not self.non_manifold_edges and not self.zero_area_faces


def fill_small_holes(name, obj, max_perimeter=2.0):
    """Close tiny boundary loops left by an exact boolean.

    The 100% south-facing plate can leave a sub-millimetre sliver hole where the
    hotswap pocket corner meets a switch leg hole.  The hole is a boolean
    artifact, not a design opening: its boundary is short and each edge has one
    face.  Fill only such small loops; larger boundary loops are left for
    :func:`check_mesh` to reject so a real modelling error stays loud.

    Returns the number of new faces.
    """

    bm = bmesh.new()
    bm.from_mesh(obj.data)

    remaining = {edge for edge in bm.edges if not edge.is_manifold}
    if not remaining:
        bm.free()
        return 0

    groups = []
    while remaining:
        start = remaining.pop()
        group = {start}
        stack = [start]
        while stack:
            edge = stack.pop()
            for vertex in edge.verts:
                for other in vertex.link_edges:
                    if other in remaining and not other.is_manifold:
                        remaining.remove(other)
                        group.add(other)
                        stack.append(other)
        groups.append(group)

    filled = 0
    for group in groups:
        if any(len(edge.link_faces) != 1 for edge in group):
            continue
        if sum(edge.calc_length() for edge in group) > max_perimeter:
            continue
        result = bmesh.ops.holes_fill(bm, edges=list(group), sides=0)
        filled += len(result.get("faces", ()))

    if filled:
        bmesh.ops.recalc_face_normals(bm, faces=bm.faces)
        bm.to_mesh(obj.data)
        obj.data.update()
        print(f"{name}: filled {filled} small boolean hole face(s)")

    bm.free()
    return filled


def check_mesh(name, obj, strict=True):
    """Check a mesh for the defects this project cares about.

    Counts non-manifold edges and zero-area faces.  In strict mode (the
    default) a bad mesh raises, so a generator never exports a part that
    violates the project's printing rules.
    """

    bm = bmesh.new()
    bm.from_mesh(obj.data)
    faces = len(bm.faces)
    non_manifold_edges = sum(1 for edge in bm.edges if not edge.is_manifold)
    zero_area_faces = sum(
        1 for face in bm.faces if face.calc_area() <= ZERO_AREA_EPSILON
    )
    bm.free()

    report = MeshReport(faces, non_manifold_edges, zero_area_faces)
    if report.ok:
        print(f"{name}: watertight mesh, {faces} faces")
        return report

    problems = []
    if not faces:
        problems.append("no faces")
    if non_manifold_edges:
        problems.append(f"{non_manifold_edges} non-manifold edges")
    if zero_area_faces:
        problems.append(f"{zero_area_faces} zero-area faces")
    message = f"{name}: invalid mesh ({', '.join(problems)})"

    if strict:
        print(f"ERROR: {message}", file=sys.stderr)
        raise RuntimeError(message)

    print(f"WARNING: {message} (check before printing)")
    return report
