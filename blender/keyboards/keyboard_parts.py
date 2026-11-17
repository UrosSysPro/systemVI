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

``PlateParams.led_facing`` selects the switch orientation.  ``"north"`` keeps
the default footprint; ``"south"`` rotates every key's cutouts by 180 degrees
around the switch centre, so the LED ends up south, the pins north and the
hotswap pocket on the opposite side.  The two states are physically the same
switch, flipped.
"""

import argparse
import math
import os
import sys
from dataclasses import dataclass

import bmesh
import bpy
from mathutils import Matrix, Vector

# --------------------------------------------------------------------------
# Layout
# --------------------------------------------------------------------------

PITCH = 19.05          # 1u
PLATE_MARGIN = 5.0     # default value for each of the four plate margins

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
LED_OFFSET_Y = 5.08              # north of the switch centre
LED_OFFSET = (0.0, LED_OFFSET_Y)  # south facing is the 180 degree switch flip

LED_FACING_NORTH = "north"       # switch in its default orientation
LED_FACING_SOUTH = "south"       # whole switch footprint rotated 180 degrees

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
class PlateParams:
    """Per-keyboard top plate parameters.

    Every margin defaults to ``PLATE_MARGIN`` (5 mm), so the generated plate is
    unchanged unless a value is overridden.  ``led_facing`` is either
    ``"north"`` or ``"south"``: south rotates the *entire* switch footprint
    (pins, legs, hotswap pocket, LED pocket and light hole) by 180 degrees
    around the switch centre, which is what flipping the switch does.
    """

    margin_left: float = PLATE_MARGIN
    margin_right: float = PLATE_MARGIN
    margin_top: float = PLATE_MARGIN
    margin_bottom: float = PLATE_MARGIN
    led_facing: str = LED_FACING_NORTH

    def __post_init__(self):
        for name in ("margin_left", "margin_right", "margin_top", "margin_bottom"):
            if getattr(self, name) < 0.0:
                raise ValueError(f"{name} must not be negative")

    def facing_angle(self):
        """Z rotation applied to every key: 0 for north, pi for south."""

        if self.led_facing == LED_FACING_NORTH:
            return 0.0
        if self.led_facing == LED_FACING_SOUTH:
            return math.pi
        raise ValueError(
            f"led_facing must be {LED_FACING_NORTH!r} or {LED_FACING_SOUTH!r}, "
            f"got {self.led_facing!r}"
        )


@dataclass
class Key:
    """A key position on the plate.

    ``x``/``y`` are the switch centre in millimetres.  ``width``/``height``
    describe the key cap footprint in millimetres and are only used to
    compute the plate outline; the switch cutout itself is always 1u.
    """

    x: float
    y: float
    width: float = PITCH
    height: float = PITCH
    rotation: float = 0.0     # radians, for future rotated keys


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
            keys.append(
                Key(
                    x + width * pitch / 2.0,
                    y,
                    width=width * pitch,
                    height=pitch,
                )
            )
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
            Key(
                x * pitch,
                y * pitch,
                width=width * pitch,
                height=height * pitch,
                rotation=rotation,
            )
        )
    return keys


def layout_bounds(keys, params=None):
    """Bounding rectangle (x_min, x_max, y_min, y_max) of all key cells.

    ``params`` carries the four independent margins: left/right along X and
    top/bottom along Y (top is +Y, the function row side).
    """

    if not keys:
        raise ValueError("layout has no keys")
    params = params or PlateParams()

    x_min = min(k.x - k.width / 2.0 for k in keys) - params.margin_left
    x_max = max(k.x + k.width / 2.0 for k in keys) + params.margin_right
    y_min = min(k.y - k.height / 2.0 for k in keys) - params.margin_bottom
    y_max = max(k.y + k.height / 2.0 for k in keys) + params.margin_top
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


def build_top_plate(keys, name="top_plate", params=None):
    """Create the top plate object for ``keys``, centred on the origin.

    ``params`` is a :class:`PlateParams` with the four margins and the LED
    facing; when omitted the defaults (5 mm margins, north facing LED) are
    used.  South facing adds 180 degrees to every key rotation, so the whole
    switch footprint is flipped, not only the LED.  The plate bottom sits on
    z = 0 and its top on z = PLATE_THICKNESS, so the exported STL drops
    straight onto the printer bed.
    """

    params = params or PlateParams()
    facing = params.facing_angle()
    thickness = PLATE_THICKNESS
    x_min, x_max, y_min, y_max = layout_bounds(keys, params)
    width = x_max - x_min
    depth = y_max - y_min
    centre_x = (x_min + x_max) / 2.0
    centre_y = (y_min + y_max) / 2.0

    placed = [
        (k.x - centre_x, k.y - centre_y, k.rotation + facing) for k in keys
    ]

    bm = bmesh.new()
    _add_box(bm, (0.0, 0.0, thickness / 2.0), (width, depth, thickness))
    plate = _mesh_object(name, bm)

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


def generate_top_plate(keys, name, output_path, params=None):
    """Reset the scene, build the top plate and export it as STL."""

    reset_scene()
    plate = build_top_plate(keys, name=name, params=params)
    dimensions = tuple(round(value, 2) for value in plate.dimensions)
    print(
        f"{name}: {len(keys)} keys, plate "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    export_stl(plate, output_path)
    return plate


# --------------------------------------------------------------------------
# Command line
# --------------------------------------------------------------------------


def script_arguments(argv=None):
    """Return the script's own arguments.

    Blender consumes everything up to ``--``; anything after it is meant for
    the ``--python`` script.  ``argv`` can be passed explicitly for testing.
    """

    if argv is not None:
        return list(argv)

    raw = sys.argv
    if "--" in raw:
        return raw[raw.index("--") + 1 :]
    return []


def plate_argument_parser(description=None):
    """Argparse parser with the shared top plate options."""

    parser = argparse.ArgumentParser(description=description)
    parser.add_argument(
        "--led-facing",
        choices=(LED_FACING_NORTH, LED_FACING_SOUTH),
        default=LED_FACING_NORTH,
        help="switch orientation, south flips the whole switch footprint "
        "(default: %(default)s)",
    )

    padding = parser.add_argument_group(
        "padding (mm)",
        "The four sides default to PLATE_MARGIN (5 mm).  Later settings win: "
        "--all, then --horizontal/--vertical, then the individual sides.",
    )
    padding.add_argument(
        "--all",
        "--padding-all",
        dest="padding_all",
        type=float,
        metavar="MM",
        help="set all four sides",
    )
    padding.add_argument(
        "--horizontal",
        "--padding-horizontal",
        dest="padding_horizontal",
        type=float,
        metavar="MM",
        help="set left and right",
    )
    padding.add_argument(
        "--vertical",
        "--padding-vertical",
        dest="padding_vertical",
        type=float,
        metavar="MM",
        help="set top and bottom",
    )
    padding.add_argument(
        "--padding-left", dest="padding_left", type=float, metavar="MM"
    )
    padding.add_argument(
        "--padding-right", dest="padding_right", type=float, metavar="MM"
    )
    padding.add_argument(
        "--padding-top", dest="padding_top", type=float, metavar="MM"
    )
    padding.add_argument(
        "--padding-bottom", dest="padding_bottom", type=float, metavar="MM"
    )

    parser.add_argument(
        "--output",
        metavar="PATH",
        help="base STL path; case and cover get _case/_cover suffixes "
        "(default: next to the keyboard script)",
    )
    return parser


def params_from_args(args):
    """Build a :class:`PlateParams` from parsed CLI arguments."""

    base = PLATE_MARGIN if args.padding_all is None else args.padding_all
    left = right = top = bottom = base

    if args.padding_horizontal is not None:
        left = right = args.padding_horizontal
    if args.padding_vertical is not None:
        top = bottom = args.padding_vertical

    if args.padding_left is not None:
        left = args.padding_left
    if args.padding_right is not None:
        right = args.padding_right
    if args.padding_top is not None:
        top = args.padding_top
    if args.padding_bottom is not None:
        bottom = args.padding_bottom

    try:
        return PlateParams(
            margin_left=left,
            margin_right=right,
            margin_top=top,
            margin_bottom=bottom,
            led_facing=args.led_facing,
        )
    except ValueError as exc:
        raise SystemExit(f"error: {exc}") from exc


def parse_plate_args(argv=None, description=None):
    """Parse the script arguments (everything after Blender's ``--``)."""

    parser = plate_argument_parser(description)
    return parser.parse_args(script_arguments(argv))


def output_paths(base_path, plate_default, case_default, cover_default):
    """Resolve the three part paths for one keyboard.

    ``--output`` (``base_path``) names the top plate STL; the case and cover
    reuse its stem with ``_case`` and ``_cover`` suffixes, so generating
    variants with different paddings/facings never overwrites another variant.
    """

    if base_path is None:
        return plate_default, case_default, cover_default

    stem, extension = os.path.splitext(base_path)
    return base_path, f"{stem}_case{extension}", f"{stem}_cover{extension}"
