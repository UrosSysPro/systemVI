"""Universal keyboard case and lid generator.

The case is a hollow tray with 45 degree chamfers on the top and bottom rims
and rounded corners.  The top plate drops into a pocket that the case wall
wraps on the left, right and bottom (south) sides.  On the top (north) side the
case extends into an electronics bay for the controller and batteries; the bay
is closed by a lid that sits flush with the case top and clamps the top plate
along its north edge, so the finished keyboard is three printed parts: top
plate, case and cover.

A USB-C slot is cut through the north wall near its left end, because the
controller always gets an extension cable routed there.

Every keyboard gets two extra parts next to its top plate::

    <name>_case.stl
    <name>_cover.stl

All dimensions are millimetres.  One Blender unit is one millimetre on STL
export, and the plate pocket uses the same X/Y coordinates as the exported top
plate, so the parts line up when opened together.
"""

import math
from dataclasses import dataclass

import bmesh

import keyboard_parts as kb

# --------------------------------------------------------------------------
# Case dimensions
# --------------------------------------------------------------------------

WALL = 4.0                 # outer wall thickness
FLOOR = 3.0                # case floor thickness
CHAMFER = 1.5              # 45 degree chamfer on the top and bottom rims
CORNER_RADIUS = 7.0        # rounded corner radius in plan view
CORNER_SEGMENTS = 8        # segments per rounded corner

PLATE_CLEARANCE = 0.25     # per side, plate pocket vs top plate
COMPARTMENT_DEPTH = 40.0   # internal north/south depth of the bay

LID_THICKNESS = 2.5        # cover thickness, top sits flush with the case
LID_LEDGE = 1.5            # shelf around the bay the lid rests on
LID_CLEARANCE = 0.2        # per side, lid vs bay opening
LID_OVERLAP = 4.0          # how far the lid reaches over the top plate

USB_PORT_WIDTH = 9.2       # USB-C connector plus body clearance
USB_PORT_HEIGHT = 3.4
USB_PORT_FROM_LEFT = 12.0  # slot centre from the bay's inner west wall

PLATE_TOP = FLOOR + kb.PLATE_THICKNESS
CASE_HEIGHT = PLATE_TOP + LID_THICKNESS


# --------------------------------------------------------------------------
# Metrics shared by the case and the cover
# --------------------------------------------------------------------------


@dataclass(frozen=True)
class CaseMetrics:
    width: float
    depth: float
    height: float
    west: float
    east: float
    south: float
    north: float
    plate_pocket: tuple       # (x0, x1, y0, y1)
    bay_opening: tuple        # (x0, x1, y0, y1)
    bay_cavity: tuple         # (x0, x1, y0, y1)
    lid: tuple                # (x0, x1, y0, y1)
    usb_center: tuple         # (x, z)


def case_metrics(keys, params=None):
    """Compute every dimension of the case for one keyboard layout."""

    params = params or kb.PlateParams()
    x_min, x_max, y_min, y_max = kb.layout_bounds(keys, params)
    half_w = (x_max - x_min) / 2.0
    half_d = (y_max - y_min) / 2.0

    # The plate is exported centred on the origin, so the pocket uses the same
    # coordinates and the parts line up in a viewer.
    pocket = (
        -half_w - PLATE_CLEARANCE,
        half_w + PLATE_CLEARANCE,
        -half_d - PLATE_CLEARANCE,
        half_d + PLATE_CLEARANCE,
    )

    west = pocket[0] - WALL
    east = pocket[1] + WALL
    south = pocket[2] - WALL

    # The bay starts at the plate pocket's north edge, so the plate is wrapped
    # by the case on three sides only and the lid clamps its fourth edge.
    bay_south = pocket[3]
    bay_north = bay_south + COMPARTMENT_DEPTH
    north = bay_north + WALL

    opening = (pocket[0], pocket[1], bay_south, bay_north)
    cavity = (
        opening[0] + LID_LEDGE,
        opening[1] - LID_LEDGE,
        opening[2],
        opening[3] - LID_LEDGE,
    )
    lid = (
        opening[0] + LID_CLEARANCE,
        opening[1] - LID_CLEARANCE,
        opening[2] - LID_OVERLAP + LID_CLEARANCE,
        opening[3] - LID_CLEARANCE,
    )
    usb_center = (
        opening[0] + USB_PORT_FROM_LEFT,
        FLOOR + (PLATE_TOP - FLOOR) / 2.0,
    )

    return CaseMetrics(
        width=east - west,
        depth=north - south,
        height=CASE_HEIGHT,
        west=west,
        east=east,
        south=south,
        north=north,
        plate_pocket=pocket,
        bay_opening=opening,
        bay_cavity=cavity,
        lid=lid,
        usb_center=usb_center,
    )


# --------------------------------------------------------------------------
# bmesh helpers
# --------------------------------------------------------------------------


def _rounded_rect_points(cx, cy, half_w, half_d, radius, segments):
    """Closed CCW rounded rectangle ring, ``segments`` per corner."""

    radius = max(radius, 0.0)
    arc_x = max(half_w - radius, 0.0)
    arc_y = max(half_d - radius, 0.0)

    corners = (
        (arc_x, -arc_y, -math.pi / 2.0, 0.0),          # bottom right
        (arc_x, arc_y, 0.0, math.pi / 2.0),            # top right
        (-arc_x, arc_y, math.pi / 2.0, math.pi),       # top left
        (-arc_x, -arc_y, math.pi, 1.5 * math.pi),      # bottom left
    )

    points = []
    for corner_x, corner_y, start, end in corners:
        for i in range(segments + 1):
            angle = start + (end - start) * i / segments
            points.append(
                (
                    cx + corner_x + radius * math.cos(angle),
                    cy + corner_y + radius * math.sin(angle),
                )
            )
    return points


def _add_loft(bm, rings):
    """Close a set of equal length rings (``(z, points)``) into a solid."""

    vertex_rings = []
    for z, points in rings:
        vertex_rings.append([bm.verts.new((x, y, z)) for x, y in points])

    bm.faces.new(tuple(vertex_rings[0]))
    bm.faces.new(tuple(reversed(vertex_rings[-1])))

    count = len(vertex_rings[0])
    for lower, upper in zip(vertex_rings, vertex_rings[1:]):
        for i in range(count):
            j = (i + 1) % count
            bm.faces.new((lower[i], lower[j], upper[j], upper[i]))


def _add_case_shell(bm, metrics):
    """Outer solid: rounded rectangle with 45 degree top and bottom chamfers."""

    centre_x = (metrics.west + metrics.east) / 2.0
    centre_y = (metrics.south + metrics.north) / 2.0
    half_w = (metrics.east - metrics.west) / 2.0
    half_d = (metrics.north - metrics.south) / 2.0

    profiles = (
        (0.0, CHAMFER),
        (CHAMFER, 0.0),
        (metrics.height - CHAMFER, 0.0),
        (metrics.height, CHAMFER),
    )

    rings = []
    for z, inset in profiles:
        points = _rounded_rect_points(
            centre_x,
            centre_y,
            half_w - inset,
            half_d - inset,
            max(CORNER_RADIUS - inset, 0.0),
            CORNER_SEGMENTS,
        )
        rings.append((z, points))

    _add_loft(bm, rings)


# --------------------------------------------------------------------------
# Cutters
# --------------------------------------------------------------------------


def _box_cutter(name, rect, z_min, z_max):
    x0, x1, y0, y1 = rect
    bm = bmesh.new()
    kb._add_box(
        bm,
        ((x0 + x1) / 2.0, (y0 + y1) / 2.0, (z_min + z_max) / 2.0),
        (x1 - x0, y1 - y0, z_max - z_min),
    )
    return kb._mesh_object(name, bm)


def _plate_pocket_cutter(metrics):
    return _box_cutter(
        "plate_pocket",
        metrics.plate_pocket,
        FLOOR,
        metrics.height + kb.EPS,
    )


def _bay_cavity_cutter(metrics):
    return _box_cutter(
        "bay_cavity",
        metrics.bay_cavity,
        FLOOR,
        metrics.height + kb.EPS,
    )


def _bay_opening_cutter(metrics):
    """Widen the top of the bay so the lid can sit on a flush shelf."""

    return _box_cutter(
        "bay_opening",
        metrics.bay_opening,
        PLATE_TOP,
        metrics.height + kb.EPS,
    )


def _usb_port_cutter(metrics):
    x, z = metrics.usb_center
    rect = (
        x - USB_PORT_WIDTH / 2.0,
        x + USB_PORT_WIDTH / 2.0,
        metrics.bay_cavity[3] - 1.0,
        metrics.north + 1.0,
    )
    return _box_cutter(
        "usb_port",
        rect,
        z - USB_PORT_HEIGHT / 2.0,
        z + USB_PORT_HEIGHT / 2.0,
    )


# --------------------------------------------------------------------------
# Part generation
# --------------------------------------------------------------------------


def _check_watertight(name, obj):
    bm = bmesh.new()
    bm.from_mesh(obj.data)
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


def build_case(keys, params=None, name="case"):
    """Build the case; the plate pocket is centred like the top plate."""

    metrics = case_metrics(keys, params)

    bm = bmesh.new()
    _add_case_shell(bm, metrics)
    case = kb._mesh_object(name, bm)

    for cutter in (
        _plate_pocket_cutter(metrics),
        _bay_cavity_cutter(metrics),
        _bay_opening_cutter(metrics),
        _usb_port_cutter(metrics),
    ):
        kb._difference(case, cutter)

    _check_watertight(name, case)
    return case


def build_cover(keys, params=None, name="cover"):
    """Build the bay lid, in its final position (flush with the case top)."""

    metrics = case_metrics(keys, params)
    x0, x1, y0, y1 = metrics.lid

    bm = bmesh.new()
    kb._add_box(
        bm,
        ((x0 + x1) / 2.0, (y0 + y1) / 2.0, metrics.height - LID_THICKNESS / 2.0),
        (x1 - x0, y1 - y0, LID_THICKNESS),
    )
    return kb._mesh_object(name, bm)


def generate_case(keys, name, output_path, params=None):
    case = build_case(keys, name=name, params=params)
    dimensions = tuple(round(value, 2) for value in case.dimensions)
    print(
        f"{name}: case "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    kb.export_stl(case, output_path)
    return case


def generate_cover(keys, name, output_path, params=None):
    cover = build_cover(keys, name=name, params=params)
    dimensions = tuple(round(value, 2) for value in cover.dimensions)
    print(
        f"{name}: cover "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    kb.export_stl(cover, output_path)
    return cover
