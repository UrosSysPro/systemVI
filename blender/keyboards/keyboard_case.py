"""Universal keyboard case and lid generator.

The case is a hollow tray with 45 degree chamfers on the top and bottom rims
and rounded corners.  The top plate drops into a pocket that the case wall
wraps on the left, right and bottom (south) sides.  On the top (north) side the
case extends into an electronics bay for the controller and batteries.

``COMPARTMENT_HEIGHT`` sets the top of that bay (default: the case rim, i.e.
not raised).  When it is higher than the case rim, the case's outer edges are
slanted over ``COMPARTMENT_RAMP`` from the rim height up to the compartment
height, so the raised part grows gradually instead of stepping.

The bay is closed by an L-shaped cover: a horizontal top plate resting on the
bay shelf, plus a vertical wall hanging down in front of the top row of keys.

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

LID_THICKNESS = 2.5        # cover top thickness
LID_LEDGE = 1.5            # shelf around the bay the cover rests on
LID_CLEARANCE = 0.2        # per side, cover vs bay opening

USB_PORT_WIDTH = 9.2       # USB-C connector plus body clearance
USB_PORT_HEIGHT = 3.4
USB_PORT_FROM_LEFT = 12.0  # slot centre from the bay's inner west wall

PLATE_TOP = FLOOR + kb.PLATE_THICKNESS
CASE_HEIGHT = PLATE_TOP + LID_THICKNESS

COMPARTMENT_HEIGHT = CASE_HEIGHT   # top of the bay, may be raised
COMPARTMENT_RAMP = 20.0            # horizontal run of the slanted edge
COVER_WALL = 2.5                   # cover front wall thickness


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
    cover: tuple              # (x0, x1, y0, y1)
    usb_center: tuple         # (x, z)


def case_metrics(keys, params=None, compartment_height=None):
    """Compute every dimension of the case for one keyboard layout."""

    height = COMPARTMENT_HEIGHT if compartment_height is None else compartment_height
    if height < CASE_HEIGHT:
        raise ValueError(
            "COMPARTMENT_HEIGHT must be at least CASE_HEIGHT "
            f"({CASE_HEIGHT} mm), got {height} mm"
        )

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
    # by the case on three sides only; the cover's front wall closes the bay
    # against the top row of keys.
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
    cover = (
        opening[0] + LID_CLEARANCE,
        opening[1] - LID_CLEARANCE,
        opening[2],
        opening[3] - LID_CLEARANCE,
    )
    usb_center = (
        opening[0] + USB_PORT_FROM_LEFT,
        FLOOR + (height - LID_THICKNESS - FLOOR) / 2.0,
    )

    return CaseMetrics(
        width=east - west,
        depth=north - south,
        height=height,
        west=west,
        east=east,
        south=south,
        north=north,
        plate_pocket=pocket,
        bay_opening=opening,
        bay_cavity=cavity,
        cover=cover,
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
    """Widen the top of the bay so the cover can sit on a flush shelf."""

    return _box_cutter(
        "bay_opening",
        metrics.bay_opening,
        metrics.height - LID_THICKNESS,
        metrics.height + kb.EPS,
    )


def _compartment_ramp_cutter(metrics, ramp):
    """Trim the south part of the case down to the rim, slanted into the bay.

    The cutter's lower face is the case rim height (15 mm) in the south and
    ramps up to the compartment height at the bay's south edge, so the raised
    part has slanted edges instead of a vertical step.
    """

    y_end = metrics.bay_opening[2]
    y_start = max(y_end - ramp, metrics.south - 5.0)
    x0 = metrics.west - 5.0
    x1 = metrics.east + 5.0
    top = metrics.height + 50.0
    far = metrics.south - 5.0

    if ramp <= 0.0:
        profile = (
            (far, CASE_HEIGHT),
            (y_end, CASE_HEIGHT),
            (y_end, top),
            (far, top),
        )
    else:
        profile = (
            (far, CASE_HEIGHT),
            (y_start, CASE_HEIGHT),
            (y_end, metrics.height),
            (y_end, top),
            (far, top),
        )

    bm = bmesh.new()
    _add_prism_x(bm, profile, x0, x1)
    return kb._mesh_object("compartment_ramp", bm)


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


def build_case(
    keys,
    params=None,
    name="case",
    compartment_height=None,
    compartment_ramp=None,
):
    """Build the case; the plate pocket is centred like the top plate."""

    ramp = COMPARTMENT_RAMP if compartment_ramp is None else compartment_ramp
    metrics = case_metrics(keys, params, compartment_height=compartment_height)

    bm = bmesh.new()
    _add_case_shell(bm, metrics)
    case = kb._mesh_object(name, bm)

    cutters = [
        _plate_pocket_cutter(metrics),
        _bay_cavity_cutter(metrics),
        _bay_opening_cutter(metrics),
        _usb_port_cutter(metrics),
    ]
    if metrics.height > CASE_HEIGHT:
        cutters.insert(0, _compartment_ramp_cutter(metrics, ramp))

    for cutter in cutters:
        kb._difference(case, cutter)

    _check_watertight(name, case)
    return case


def _add_prism_x(bm, profile_yz, x0, x1):
    """Extrude a closed Y/Z profile along X into a solid."""

    ring0 = [bm.verts.new((x0, y, z)) for y, z in profile_yz]
    ring1 = [bm.verts.new((x1, y, z)) for y, z in profile_yz]

    bm.faces.new(tuple(ring0))
    bm.faces.new(tuple(reversed(ring1)))

    count = len(profile_yz)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((ring0[i], ring0[j], ring1[j], ring1[i]))


def build_cover(
    keys,
    params=None,
    name="cover",
    compartment_height=None,
):
    """Build the cover with an L-shaped side profile.

    The horizontal leg is the top plate that rests on the bay shelf; the
    vertical leg is the wall hanging down in front of the top row of keys,
    reaching the top plate surface.  When the compartment is raised the case
    carries the slanted edges, so the cover itself stays a clean L.
    """

    height = COMPARTMENT_HEIGHT if compartment_height is None else compartment_height

    metrics = case_metrics(keys, params, compartment_height=height)
    x0, x1, y0, y1 = metrics.cover
    z_bottom = PLATE_TOP
    z_top = height

    profile = (
        (y0, z_bottom),
        (y0, z_top),
        (y1, z_top),
        (y1, z_top - LID_THICKNESS),
        (y0 + COVER_WALL, z_top - LID_THICKNESS),
        (y0 + COVER_WALL, z_bottom),
    )

    bm = bmesh.new()
    _add_prism_x(bm, profile, x0, x1)
    cover = kb._mesh_object(name, bm)

    _check_watertight(name, cover)
    return cover


def generate_case(
    keys,
    name,
    output_path,
    params=None,
    compartment_height=None,
    compartment_ramp=None,
):
    case = build_case(
        keys,
        name=name,
        params=params,
        compartment_height=compartment_height,
        compartment_ramp=compartment_ramp,
    )
    dimensions = tuple(round(value, 2) for value in case.dimensions)
    print(
        f"{name}: case "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    kb.export_stl(case, output_path)
    return case


def generate_cover(
    keys,
    name,
    output_path,
    params=None,
    compartment_height=None,
):
    cover = build_cover(
        keys,
        name=name,
        params=params,
        compartment_height=compartment_height,
    )
    dimensions = tuple(round(value, 2) for value in cover.dimensions)
    print(
        f"{name}: cover "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    kb.export_stl(cover, output_path)
    return cover
