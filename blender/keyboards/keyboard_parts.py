"""Shared generator parts for 3D printed keyboard top plates.

A top plate is one thick slab (``PLATE_THICKNESS`` mm).  Every cutout is the
same for every keyboard; only the arrangement of the keys differs:

* from the **top**: a stepped 5 pin MX switch pocket.  A 14 x 14 mm band at
  the surface, then 1 mm below it a 15 x 14 mm relief so the horizontal
  switch clips can spring back out and lock the switch in place.  The pocket
  is deep enough (``MX_POCKET_DEPTH``) to enclose the complete lower housing,
  plus the through holes for the centre post and the two plastic legs;
* from the **bottom**: a pocket for the WS2812B RGB LED, a straight (X axis
  aligned) pocket for the Gateron/Kailh hotswap socket, and a light hole from
  the LED through the plate so the LED is not covered.  The switch pins pass
  through the floor (``WEB_THICKNESS``) between the two layers, and their
  holes are large enough for the round hotswap socket contacts.

Because the switch pocket, the floor, the hotswap socket pocket and the LED
pocket are stacked, the plate is much thicker than a normal 1.5 mm plate:
it encloses the complete lower switch housing, the LED and the hotswap pad.
Only the 15.6 mm upper switch housing and the stem sit above the plate.

Layouts are described in keyboard units (1u = 19.05 mm) and converted to
millimetres here.  All dimensions are millimetres; one Blender unit is one
millimetre on STL export.
"""

import math
import os
from dataclasses import dataclass

import bmesh
import bpy
from mathutils import Matrix, Vector

# --------------------------------------------------------------------------
# Layout
# --------------------------------------------------------------------------

PITCH = 19.05          # 1u
PLATE_MARGIN = 5.0     # material from the outermost key cell to the plate edge

# --------------------------------------------------------------------------
# MX switch (5 pin), seen from the switch centre; +y points "north"
# --------------------------------------------------------------------------

MX_POCKET = 14.0                 # switch pocket band at the plate surface
MX_POCKET_CLIP_X = 15.0          # clip relief below the band, X direction
MX_POCKET_CLIP_Y = 14.0          # clip relief below the band, Y direction
CLIP_RELIEF_DEPTH = 1.0          # 14x14 band depth before the clips expand
MX_POCKET_DEPTH = 5.0            # encloses the whole lower switch housing
MX_CENTER_POST_DIAMETER = 4.2    # centre post is 4.0 mm
MX_PIN_POSITIONS = (             # the two metal pins
    (-3.81, -2.54),
    (2.54, -5.08),
)
MX_PIN_HOLE_DIAMETER = 3.2       # fits the round hotswap socket contacts
MX_LEG_POSITIONS = (             # the two extra plastic legs of a 5 pin switch
    (-5.08, 0.0),
    (5.08, 0.0),
)
MX_LEG_HOLE_DIAMETER = 2.0

# --------------------------------------------------------------------------
# Hotswap socket (Gateron / Kailh type), one per switch
# --------------------------------------------------------------------------

# Straight, X axis aligned pocket; centred between both switch pins.
SOCKET_POCKET_LENGTH = 12.0      # X
SOCKET_POCKET_WIDTH = 9.0        # Y
SOCKET_POCKET_DEPTH = 3.0

# --------------------------------------------------------------------------
# WS2812B RGB LED (5050 package)
# --------------------------------------------------------------------------

LED_POCKET = 5.2                 # 5.0 mm package plus clearance
LED_POCKET_DEPTH = 1.7
LED_LIGHT_HOLE_DIAMETER = 3.6    # lets the LED shine through the plate
LED_OFFSET = (0.0, 5.08)         # north of the switch centre

# --------------------------------------------------------------------------
# Derived thickness
# --------------------------------------------------------------------------

WEB_THICKNESS = 1.5              # solid floor between switch and socket/LED
PLATE_THICKNESS = MX_POCKET_DEPTH + WEB_THICKNESS + SOCKET_POCKET_DEPTH

EPS = 0.5                        # cutters overshoot the plate faces


# --------------------------------------------------------------------------
# Layout helpers
# --------------------------------------------------------------------------


@dataclass
class Key:
    """A key position on the plate.

    ``x``/``y`` are the switch centre in millimetres.  ``width``/``height``
    describe the key cap footprint in keyboard units and are only used to
    compute the plate outline; the switch cutout itself is always 1u.
    """

    x: float
    y: float
    width: float = 1.0
    height: float = 1.0
    rotation: float = 0.0     # radians, for future rotated keys

    @property
    def unit_x(self):
        return self.x / PITCH

    @property
    def unit_y(self):
        return self.y / PITCH


def keys_from_rows(rows, pitch=PITCH):
    """Build keys from a list of rows of key widths (in keyboard units).

    Rows are laid out from top to bottom, keys from left to right.  ``None``
    inserts a 1u gap.  The first row is centred at y = 0 and y decreases by
    one pitch per row, so the result is directly in Blender coordinates.
    """

    keys = []
    y = 0.0
    for row in rows:
        x = 0.0
        for entry in row:
            if entry is None:
                x += pitch
                continue
            width = float(entry)
            keys.append(Key(x + width * pitch / 2.0, y, width=width))
            x += width * pitch
        y -= pitch
    return keys


def keys_from_units(points, pitch=PITCH):
    """Build keys from explicit unit coordinates.

    Each point is ``(x, y)``, ``(x, y, width)``, ``(x, y, width, height)`` or
    ``(x, y, width, height, rotation_degrees)``, where x/y are the switch
    centre in keyboard units (1u = ``pitch``).  Wide or tall keys only need
    their width/height for the plate outline; the cutout stays single.
    """

    keys = []
    for point in points:
        x, y = float(point[0]), float(point[1])
        width = float(point[2]) if len(point) > 2 else 1.0
        height = float(point[3]) if len(point) > 3 else 1.0
        rotation = math.radians(point[4]) if len(point) > 4 else 0.0
        keys.append(
            Key(x * pitch, y * pitch, width=width, height=height, rotation=rotation)
        )
    return keys


def layout_bounds(keys, margin=PLATE_MARGIN):
    """Bounding rectangle (x_min, x_max, y_min, y_max) of all key cells."""

    if not keys:
        raise ValueError("layout has no keys")

    x_min = min(k.x - k.width * PITCH / 2.0 for k in keys) - margin
    x_max = max(k.x + k.width * PITCH / 2.0 for k in keys) + margin
    y_min = min(k.y - k.height * PITCH / 2.0 for k in keys) - margin
    y_max = max(k.y + k.height * PITCH / 2.0 for k in keys) + margin
    return x_min, x_max, y_min, y_max


# --------------------------------------------------------------------------
# bmesh primitives
# --------------------------------------------------------------------------


def _rotate(offset, angle):
    """Rotate a 2D offset by ``angle`` radians."""

    c = math.cos(angle)
    s = math.sin(angle)
    return (offset[0] * c - offset[1] * s, offset[0] * s + offset[1] * c)


def _add_box(bm, center, size, rotation=0.0):
    """Append an axis aligned (then Z rotated) box to ``bm``."""

    ret = bmesh.ops.create_cube(bm, size=1.0)
    verts = ret["verts"]
    matrix = (
        Matrix.Translation(Vector(center))
        @ Matrix.Rotation(rotation, 4, "Z")
        @ Matrix.Diagonal(Vector((size[0], size[1], size[2], 1.0)))
    )
    bmesh.ops.transform(bm, matrix=matrix, verts=verts)


def _add_cylinder(bm, center_xy, radius, z_min, z_max, segments=24):
    """Append a closed vertical cylinder to ``bm``."""

    cx, cy = center_xy
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


def _mesh_object(name, bm):
    """Turn a bmesh into a linked scene object with outward facing normals."""

    bmesh.ops.recalc_face_normals(bm, faces=bm.faces)
    mesh = bpy.data.meshes.new(name)
    bm.to_mesh(mesh)
    bm.free()
    obj = bpy.data.objects.new(name, mesh)
    bpy.context.scene.collection.objects.link(obj)
    return obj


# --------------------------------------------------------------------------
# Boolean helpers
# --------------------------------------------------------------------------


def _apply_modifiers(obj):
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


def _difference(target, cutter):
    """Subtract ``cutter`` from ``target`` and delete the cutter."""

    modifier = target.modifiers.new("cut", "BOOLEAN")
    modifier.operation = "DIFFERENCE"
    modifier.solver = "EXACT"
    modifier.object = cutter
    _apply_modifiers(target)

    cutter_mesh = cutter.data
    bpy.data.objects.remove(cutter, do_unlink=True)
    bpy.data.meshes.remove(cutter_mesh)


# --------------------------------------------------------------------------
# Cutout sets, shared by every keyboard
# --------------------------------------------------------------------------

# The hotswap socket sits over both switch pins.  Its pocket is centred on the
# midpoint between the pins and is kept parallel to the X axis.
_SOCKET_MID_OFFSET = (
    (MX_PIN_POSITIONS[0][0] + MX_PIN_POSITIONS[1][0]) / 2.0,
    (MX_PIN_POSITIONS[0][1] + MX_PIN_POSITIONS[1][1]) / 2.0,
)


def _add_switch_pocket(bm, x, y, rotation, thickness):
    """Stepped MX pocket: 14x14 at the surface, 15x14 below the clip band."""

    x_inner = MX_POCKET / 2.0
    x_outer = MX_POCKET_CLIP_X / 2.0
    y_half = MX_POCKET_CLIP_Y / 2.0
    z_bottom = thickness - MX_POCKET_DEPTH
    z_step = thickness - CLIP_RELIEF_DEPTH
    z_top = thickness + EPS

    # Closed profile in the XZ plane, extruded along Y.
    profile = (
        (-x_outer, z_bottom),
        (x_outer, z_bottom),
        (x_outer, z_step),
        (x_inner, z_step),
        (x_inner, z_top),
        (-x_inner, z_top),
        (-x_inner, z_step),
        (-x_outer, z_step),
    )

    cos_r = math.cos(rotation)
    sin_r = math.sin(rotation)
    rings = []
    for offset_y in (-y_half, y_half):
        ring = []
        for offset_x, z in profile:
            wx = x + offset_x * cos_r - offset_y * sin_r
            wy = y + offset_x * sin_r + offset_y * cos_r
            ring.append(bm.verts.new((wx, wy, z)))
        rings.append(ring)

    # The winding of the caps and sides is fixed by recalc_face_normals later.
    bm.faces.new(tuple(rings[0]))
    bm.faces.new(tuple(reversed(rings[1])))
    count = len(profile)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((rings[0][i], rings[0][j], rings[1][j], rings[1][i]))


def _switch_pocket_cutter(placed, thickness):
    bm = bmesh.new()
    for x, y, rotation in placed:
        _add_switch_pocket(bm, x, y, rotation, thickness)
    return _mesh_object("switch_pockets", bm)


def _socket_pocket_cutter(placed):
    bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(_SOCKET_MID_OFFSET, rotation)
        _add_box(
            bm,
            (x + ox, y + oy, (SOCKET_POCKET_DEPTH - EPS) / 2.0),
            (SOCKET_POCKET_LENGTH, SOCKET_POCKET_WIDTH, SOCKET_POCKET_DEPTH + EPS),
            rotation,
        )
    return _mesh_object("socket_pockets", bm)


def _led_pocket_cutter(placed):
    bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(LED_OFFSET, rotation)
        _add_box(
            bm,
            (x + ox, y + oy, (LED_POCKET_DEPTH - EPS) / 2.0),
            (LED_POCKET, LED_POCKET, LED_POCKET_DEPTH + EPS),
            rotation,
        )
    return _mesh_object("led_pockets", bm)


def _led_light_hole_cutter(placed, thickness):
    """Open the plate above each LED so the light can shine through."""

    bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(LED_OFFSET, rotation)
        _add_cylinder(
            bm,
            (x + ox, y + oy),
            LED_LIGHT_HOLE_DIAMETER / 2.0,
            -EPS,
            thickness + EPS,
        )
    return _mesh_object("led_light_holes", bm)


def _hole_cutter(placed, thickness):
    """Centre post, switch pins and 5 pin plastic legs, through the floor."""

    z_min = -EPS
    z_max = thickness + EPS
    bm = bmesh.new()
    for x, y, rotation in placed:
        _add_cylinder(
            bm, (x, y), MX_CENTER_POST_DIAMETER / 2.0, z_min, z_max
        )
        for px, py in MX_PIN_POSITIONS:
            ox, oy = _rotate((px, py), rotation)
            _add_cylinder(
                bm, (x + ox, y + oy), MX_PIN_HOLE_DIAMETER / 2.0, z_min, z_max
            )
        for px, py in MX_LEG_POSITIONS:
            ox, oy = _rotate((px, py), rotation)
            _add_cylinder(
                bm, (x + ox, y + oy), MX_LEG_HOLE_DIAMETER / 2.0, z_min, z_max
            )
    return _mesh_object("holes", bm)


# --------------------------------------------------------------------------
# Plate generation
# --------------------------------------------------------------------------


def build_top_plate(keys, name="top_plate", margin=PLATE_MARGIN):
    """Create the top plate object for ``keys``, centred on the origin.

    The plate bottom sits on z = 0 and its top on z = PLATE_THICKNESS, so the
    exported STL drops straight onto the printer bed.
    """

    thickness = PLATE_THICKNESS
    x_min, x_max, y_min, y_max = layout_bounds(keys, margin)
    width = x_max - x_min
    depth = y_max - y_min
    centre_x = (x_min + x_max) / 2.0
    centre_y = (y_min + y_max) / 2.0

    placed = [(k.x - centre_x, k.y - centre_y, k.rotation) for k in keys]

    bm = bmesh.new()
    _add_box(bm, (0.0, 0.0, thickness / 2.0), (width, depth, thickness))
    plate = _mesh_object(name, bm)
    plate.data.name = name

    cutters = [
        _switch_pocket_cutter(placed, thickness),
        _socket_pocket_cutter(placed),
        _led_pocket_cutter(placed),
        _led_light_hole_cutter(placed, thickness),
        _hole_cutter(placed, thickness),
    ]
    for cutter in cutters:
        _difference(plate, cutter)

    bm = bmesh.new()
    bm.from_mesh(plate.data)
    faces = len(bm.faces)
    open_edges = sum(1 for edge in bm.edges if not edge.is_manifold)
    bm.free()

    if open_edges:
        print(
            f"WARNING: {name} has {open_edges} non-manifold edges "
            "(check before printing)"
        )
    else:
        print(f"{name}: watertight mesh, {faces} faces")

    return plate


def reset_scene():
    """Start from an empty scene, independent of Blender's startup file."""

    bpy.ops.wm.read_factory_settings(use_empty=True)


def export_stl(obj, path):
    """Export ``obj`` to a binary STL and verify that it was written."""

    directory = os.path.dirname(os.path.abspath(path))
    os.makedirs(directory, exist_ok=True)
    if os.path.exists(path):
        os.remove(path)

    bpy.ops.object.select_all(action="DESELECT")
    obj.select_set(True)
    bpy.context.view_layer.objects.active = obj

    if hasattr(bpy.ops.wm, "stl_export"):
        bpy.ops.wm.stl_export(
            filepath=path,
            export_selected_objects=True,
            ascii_format=False,
            apply_modifiers=True,
        )
    else:
        bpy.ops.export_mesh.stl(
            filepath=path,
            use_selection=True,
            ascii=False,
            apply_modifiers=True,
        )

    if not os.path.isfile(path) or os.path.getsize(path) == 0:
        raise RuntimeError(f"STL export failed, nothing written to {path}")

    print(f"Exported {path} ({os.path.getsize(path)} bytes)")


def generate_top_plate(keys, name, output_path, margin=PLATE_MARGIN):
    """Reset the scene, build the top plate and export it as STL."""

    reset_scene()
    plate = build_top_plate(keys, name=name, margin=margin)
    dimensions = tuple(round(value, 2) for value in plate.dimensions)
    print(
        f"{name}: {len(keys)} keys, plate "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    export_stl(plate, output_path)
    return plate
