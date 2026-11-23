"""Split flat keyboard case with a top electronics bay.

The case is printed as two parts that share the existing outer silhouette:

- :func:`build_case_bottom` is the bottom tray.  Its footprint is inset behind
  the edge ring, so only its underside is visible.  It carries the wire recess,
  the inter-key support ribs, an upstanding perimeter lip, two countersunk
  plate-screw holes, four countersunk north-edge holes and a triangular dent in
  its south side.
- :func:`build_case_edges` is the full-height outer edge ring plus the bay,
  USB-C opening and cover magnet shelves.  From the outside it is unchanged.
  Inside it has a lower recess that fits over the bottom tray's lip, an
  overhanging shelf above the plate tabs, four screw boxes on the north wall
  and a wide triangular ledge on the south wall.

The top plate has 4 mm lower side tabs.  A tab sits on the bottom tray and
under the edge ring's shelf.  Two 3 mm screws enter from below the tray into
blind pilots in the plate; four more enter along the north edge into the screw
boxes.  The triangular ledge on the south wall fits the tray's side dent.  The
edge ring and bottom tray bottom faces are coplanar.

``DEFAULT_COMPARTMENT_HEIGHT`` sets the top of the bay.  The default is a
raised 27.5 mm compartment; the case rim/wall remains at 20 mm.  When the
compartment is higher than the rim, the case's outer edges are slanted over
``COMPARTMENT_RAMP`` from the rim height up to the compartment height.

This module builds the two case parts only.  The separate cover lives in
:mod:`cover`; both share :func:`case_metrics` so they always fit the same bay.
All dimensions are millimetres; one Blender unit is one millimetre on STL
export.
"""

from __future__ import annotations

import math
import os
from dataclasses import dataclass

import bmesh

import hardware
import layout
import mesh
import mounting

# --------------------------------------------------------------------------
# Case dimensions
# --------------------------------------------------------------------------

WALL = 7.0                 # outer wall thickness
FLOOR = 4.0                # case floor thickness (countersunk screw heads)
CHAMFER = 2.0              # 45 degree chamfer on the top rim
BOTTOM_CHAMFER = 2.0       # 45 degree chamfer on the bottom rim
CORNER_RADIUS = 9.0        # rounded corner radius in plan view
CORNER_SEGMENTS = 8        # segments per rounded corner

PLATE_CLEARANCE = 0.25     # per side, plate pocket vs top plate
COMPARTMENT_DEPTH = 27.5   # internal north/south depth of the bay

LID_THICKNESS = 2.5        # minimum cover top thickness; magnets can raise it
LID_LEDGE = 1.5            # shelf around the bay the cover rests on
LID_CLEARANCE = 0.2        # per side, cover vs bay opening

USB_PORT_WIDTH = 9.2       # USB-C plug opening
USB_PORT_HEIGHT = 3.4
USB_PORT_RADIUS = 1.0      # rounded rectangle corner radius
USB_RING_WIDTH = 1.5       # flat seat ring around the port
USB_BEVEL = 2.0            # 45 degree bevel depth and size difference
USB_RECESS_WIDTH = (
    USB_PORT_WIDTH + 2.0 * USB_RING_WIDTH + 2.0 * USB_BEVEL
)
USB_RECESS_HEIGHT = (
    USB_PORT_HEIGHT + 2.0 * USB_RING_WIDTH + 2.0 * USB_BEVEL
)
USB_RECESS_RADIUS = USB_PORT_RADIUS + USB_RING_WIDTH + USB_BEVEL
USB_BEVEL_INNER_WIDTH = USB_PORT_WIDTH + 2.0 * USB_RING_WIDTH
USB_BEVEL_INNER_HEIGHT = USB_PORT_HEIGHT + 2.0 * USB_RING_WIDTH
USB_BEVEL_INNER_RADIUS = USB_PORT_RADIUS + USB_RING_WIDTH
USB_RECESS_DEPTH = USB_BEVEL
USB_PLATFORM_WIDTH = USB_RECESS_WIDTH
USB_PLATFORM_DEPTH = 6.0   # inward shelf depth for glue
USB_PLATFORM_THICKNESS = 2.5
USB_PLATFORM_EMBED = 0.5   # overlap into the inner wall for a clean union
USB_PLATFORM_TOP_OVERLAP = 0.2  # shaved flush by the through opening cutter
USB_PORT_FROM_LEFT = 12.0  # slot centre from the bay's inner west wall

PLATE_TOP = FLOOR + hardware.PLATE_THICKNESS
CASE_HEIGHT = 20.0  # outer case rim / wall height (lower edge)

# Split-case location.  All extents are measured outward from the plate pocket
# rectangle (which is the plate outline plus PLATE_CLEARANCE).  The lower
# recess is stepped because the case's 1.5 mm bottom chamfer leaves less room
# for the outer wall at z = 0.
TAB_TOP = FLOOR + mounting.TAB_HEIGHT       # top of the plate tabs
BOTTOM_EXT = 4.25                            # bottom tray footprint
EDGE_LOWER_EXT = 4.45                        # edge lower recess
# The lip runs from the tray's outer face back toward the plate, closing the
# void the plate's edge used to hang over.  The tray's outer face is
# BOTTOM_EXT + PLATE_CLEARANCE = 4.5 mm from the plate edge, so 4.0 mm leaves
# a 0.5 mm running clearance: the plate pocket (0.25 mm) and the edge-ring
# lower recess (0.2 mm) can never make the lip and the plate touch.  The lip
# is interrupted at every plate tab, which then passes through the cutout.
BOTTOM_LIP_WIDTH = 4.0                       # upstanding perimeter lip
BOTTOM_LIP_OVERLAP = 0.5                     # into the bottom tray
BOTTOM_LIP_CLEARANCE = 0.2                   # below the edge shelf
# Side clearance of each lip cutout around a plate tab.  The plate has 0.25 mm
# pocket play and the tray 0.2 mm lower-recess play, so the cutouts need more
# than 0.2 mm to let the tray slide past tabs that are not perfectly centred.
BOTTOM_LIP_TAB_CLEARANCE = 0.5
SPLIT_MIN_WALL = 7.0                         # radial room for tabs and recess
WIRE_RECESS_INSET = 0.5                      # solid rim under the plate edge
BOTTOM_RADIUS = 6.25                         # bottom tray footprint
EDGE_LOWER_RADIUS = 6.45                     # edge lower recess

# --------------------------------------------------------------------------
# North-edge screw boxes and the front triangular key
# --------------------------------------------------------------------------
#
# Four thin wood screws enter from below the tray along the north edge and
# thread into small boxes that thicken the lower edge-ring wall.  The boxes
# fill the thin lower wall (z = 4 .. 8) and continue into the wall above the
# tab shelf, so each screw has a solid 7.5 mm of material.
EDGE_SCREW_X_FRACTIONS = (-0.42, -0.14, 0.14, 0.42)
EDGE_SCREW_Y_OFFSET = 0.5    # screw centre north of the bay's front edge
EDGE_BOX_WIDTH = 10.0
EDGE_BOX_TOP = 12.0
EDGE_BOX_SCREW_DEPTH = 7.5
EDGE_BOX_LIP_CLEARANCE = 0.5
EDGE_BOX_EMBED = 0.5         # overlap into the north wall for a clean union

# The front key is a wide triangular ledge on the inner edge-ring wall.  Its
# vertical cross-section is a right triangle: a flat top, a 45-degree
# underside running back to the wall, and the normal wall below it.  The tray
# has a matching triangular dent in its south side, open at the top so the
# edge ring can be lowered onto the tray.
FRONT_WEDGE_WIDTH_FRACTION = 0.8
FRONT_WEDGE_DEPTH = 2.0      # ledge depth into the tray side
FRONT_WEDGE_HEIGHT = 2.0     # 45-degree underside; depth == height
FRONT_WEDGE_EMBED = 0.5      # overlap into the edge-ring wall
FRONT_WEDGE_CLEARANCE = 0.2  # around the triangular dent
FRONT_WEDGE_BOTTOM = 2.3     # unchanged height above the floor
FRONT_WEDGE_TOP = FRONT_WEDGE_BOTTOM + FRONT_WEDGE_HEIGHT

# Wide bottom trays are printed as two halves.  The single cut runs along X = 0
# and carries several dovetails in the middle of the tray.  The dovetails sit in
# the recessed plate-pocket floor, so their height follows the wire gap rather
# than the full 3 mm bed.
BOTTOM_SPLIT_MIN_WIDTH = 50.0    # split above this case width
BOTTOM_SPLIT_MAX_WIDTH = 250.0   # target maximum printable half width
BOTTOM_SEAM_GAP = 0.2            # total gap between the two halves
# Female slot.  The slot keeps the nominal shape; the male tenon below is sized
# to fit inside it.
BOTTOM_DOVETAIL_DEPTH = 4.65     # slot reach = tenon + 0.15
BOTTOM_DOVETAIL_NECK = 4.9      # slot width = tenon + 0.30 (0.15 per side)
BOTTOM_DOVETAIL_TIP = 7.8       # slot width = tenon + 0.30 (0.15 per side)
BOTTOM_DOVETAIL_CLEARANCE = 0.15  # per side around the male tenon
# Male tenon.  It is deliberately smaller than the slot in X and Y so the
# printed tray halves do not bind; the slot keeps the nominal shape above.
BOTTOM_TENON_DEPTH = 4.5         # tenon reach into the opposite half
BOTTOM_DOVETAIL_TARGET_SPACING = 30.0
BOTTOM_DOVETAIL_SPAN_FRACTION = 0.6  # share of the plate depth that gets tenons
BOTTOM_DOVETAIL_MIN_WEB = 5.0    # material between two neighbouring tenons
BOTTOM_PIECE_SUFFIXES = ("west", "east")

MAGNET_SHELF_EDGE = 1.5    # material under the magnet pocket at the shelf edge
DEFAULT_COMPARTMENT_HEIGHT = 30.0  # raised bay height
COMPARTMENT_HEIGHT = DEFAULT_COMPARTMENT_HEIGHT  # compatibility name
COMPARTMENT_RAMP = 20.0    # horizontal run of the slanted edge
COVER_WALL = 2.5           # cover front wall thickness
# Extra set-back of the lid ledge beyond the cover's front wall and the
# magnet-rail gap, so the ledge ends clear of the cover's front lip.
LID_LEDGE_SETBACK = 2.0

CUTTER_MARGIN = 5.0        # side/south overshoot of the ramp cutter
CUTTER_HEIGHT = 50.0       # top overshoot of the ramp cutter
USB_CUTTER_OVERLAP = 1.0   # overshoot through the north wall


@dataclass(frozen=True)
class CaseMetrics:
    """Dimensions shared by the case and the cover."""

    width: float
    depth: float
    height: float
    west: float
    east: float
    south: float
    north: float
    plate_pocket: layout.Rect
    bay_opening: layout.Rect
    bay_cavity: layout.Rect
    cover: layout.Rect
    usb_center: tuple
    lid_thickness: float
    shelf: float
    magnets: mounting.MagnetPlan
    ribs: tuple[layout.Rect, ...]
    tabs: mounting.TabPlan
    bottom: layout.Rect
    edge_lower: layout.Rect
    tab_top: float
    plate_screws: tuple
    edge_screws: tuple


def case_metrics(
    keys,
    params=None,
    compartment_height=None,
    wall=None,
    compartment_depth=None,
    options=None,
):
    """Compute every dimension of the case for one keyboard layout."""

    height = (
        DEFAULT_COMPARTMENT_HEIGHT
        if compartment_height is None
        else compartment_height
    )
    if not math.isfinite(height) or height < CASE_HEIGHT:
        raise ValueError(
            "compartment_height must be at least CASE_HEIGHT "
            f"({CASE_HEIGHT} mm), got {height} mm"
        )

    wall = WALL if wall is None else wall
    bay_depth = COMPARTMENT_DEPTH if compartment_depth is None else compartment_depth
    if not math.isfinite(wall) or wall <= 0.0:
        raise ValueError(f"wall must be a positive number, got {wall}")
    if wall < SPLIT_MIN_WALL:
        raise ValueError(
            "wall must be at least "
            f"{SPLIT_MIN_WALL} mm for the split case's lower recess and plate "
            f"tabs; got {wall} mm"
        )
    if not math.isfinite(bay_depth) or bay_depth <= 0.0:
        raise ValueError(
            f"compartment_depth must be a positive number, got {bay_depth}"
        )

    params = params or layout.PlateParams()
    options = options or mounting.MountingOptions()
    options.validate(FLOOR)
    lid_thickness = LID_THICKNESS
    boss_height = options.magnet_boss_height(
        options.magnet_thickness, lid_thickness
    )
    if (
        height - lid_thickness - boss_height
        < PLATE_TOP + 0.5
    ):
        raise ValueError(
            "compartment_height must leave room for the plate and the cover "
            f"magnet bosses: need at least "
            f"{PLATE_TOP + lid_thickness + boss_height + 0.5} mm, "
            f"got {height} mm"
        )

    bounds = layout.layout_bounds(keys, params)
    half_w = bounds.width / 2.0
    half_d = bounds.height / 2.0

    # The plate is exported centred on the origin, so the pocket uses the same
    # coordinates and the parts line up in a viewer.
    pocket = layout.Rect(
        -half_w - PLATE_CLEARANCE,
        half_w + PLATE_CLEARANCE,
        -half_d - PLATE_CLEARANCE,
        half_d + PLATE_CLEARANCE,
    )

    west = pocket.x0 - wall
    east = pocket.x1 + wall
    south = pocket.y0 - wall

    # The bay starts at the plate pocket's north edge, so the plate is wrapped
    # by the case on three sides only; the cover's front wall closes the bay
    # against the top row of keys.
    bay_south = pocket.y1
    bay_north = bay_south + bay_depth
    north = bay_north + wall

    opening = layout.Rect(pocket.x0, pocket.x1, bay_south, bay_north)
    cavity = layout.Rect(
        opening.x0 + LID_LEDGE,
        opening.x1 - LID_LEDGE,
        opening.y0,
        opening.y1 - LID_LEDGE,
    )
    cover = layout.Rect(
        opening.x0 + LID_CLEARANCE,
        opening.x1 - LID_CLEARANCE,
        opening.y0,
        opening.y1 - LID_CLEARANCE,
    )
    usb_center = (
        opening.x0 + USB_PORT_FROM_LEFT,
        FLOOR + (height - lid_thickness - FLOOR) / 2.0,
    )
    shelf = height - lid_thickness
    north_start = usb_center[0] + USB_RECESS_WIDTH / 2.0 + 0.5
    magnets = mounting.magnet_plan(
        opening,
        cavity,
        shelf,
        options,
        COVER_WALL,
        north_start,
    )
    for mount in magnets.mounts:
        edge = mount.pocket_depth + MAGNET_SHELF_EDGE
        if mount.rail_top - edge - mount.shelf_width < FLOOR:
            raise ValueError(
                "the 45-degree magnet shelves would reach below the case "
                "floor; reduce a magnet dimension or increase "
                "compartment_height"
            )
    ribs = mounting.rib_plan(keys, bounds)

    tabs = mounting.plate_tab_plan(bounds.width, bounds.height)
    bottom = layout.Rect(
        pocket.x0 - BOTTOM_EXT,
        pocket.x1 + BOTTOM_EXT,
        pocket.y0 - BOTTOM_EXT,
        bay_north + BOTTOM_EXT,
    )
    edge_lower = layout.Rect(
        pocket.x0 - EDGE_LOWER_EXT,
        pocket.x1 + EDGE_LOWER_EXT,
        pocket.y0 - EDGE_LOWER_EXT,
        bay_north + EDGE_LOWER_EXT,
    )
    width = east - west
    edge_screws = tuple(
        (
            fraction * width,
            bay_north + EDGE_SCREW_Y_OFFSET,
        )
        for fraction in EDGE_SCREW_X_FRACTIONS
    )

    return CaseMetrics(
        width=width,
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
        lid_thickness=lid_thickness,
        shelf=shelf,
        magnets=magnets,
        ribs=ribs,
        tabs=tabs,
        bottom=bottom,
        edge_lower=edge_lower,
        tab_top=TAB_TOP,
        plate_screws=hardware.plate_screw_plan(keys, params),
        edge_screws=edge_screws,
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


def _add_loft_y(bm, rings):
    """Close equal-length X/Z rings (``(y, points)``) into a solid."""

    vertex_rings = []
    for y, points in rings:
        vertex_rings.append([bm.verts.new((x, y, z)) for x, z in points])

    bm.faces.new(tuple(vertex_rings[0]))
    bm.faces.new(tuple(reversed(vertex_rings[-1])))

    count = len(vertex_rings[0])
    for lower, upper in zip(vertex_rings, vertex_rings[1:]):
        for i in range(count):
            j = (i + 1) % count
            bm.faces.new((lower[i], lower[j], upper[j], upper[i]))


def _add_case_shell(bm, metrics):
    """Outer solid: rounded rectangle with a 45 degree bottom chamfer.

    The top rim is left square here and chamfered in :func:`_bevel_top_rim`
    after the raised-compartment ramp has been cut.  Building the top chamfer
    up front would make the ramp cutter slice through it, so the slope would
    end in a sharp edge and the chamfer would taper to a point.
    """

    centre_x = (metrics.west + metrics.east) / 2.0
    centre_y = (metrics.south + metrics.north) / 2.0
    half_w = (metrics.east - metrics.west) / 2.0
    half_d = (metrics.north - metrics.south) / 2.0

    profiles = (
        (0.0, BOTTOM_CHAMFER),
        (BOTTOM_CHAMFER, 0.0),
        (metrics.height, 0.0),
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


def _bevel_top_rim(case, metrics):
    """Chamfer the whole outer top rim with one continuous 45 degree bevel.

    The shell is built square-edged at ``metrics.height``; the ramp cutter
    lowers the key-side part of the rim to ``CASE_HEIGHT`` and forms the sloped
    transition.  Beveling every outer-plan edge that separates the vertical
    wall from an upward-facing top surface in one operation keeps the chamfer
    continuous across the flat lower rim, the slope and the raised compartment.
    A separate bevel of only the flat rim would end in a point where the slope
    starts.
    """

    centre_x = (metrics.west + metrics.east) / 2.0
    centre_y = (metrics.south + metrics.north) / 2.0
    half_w = (metrics.east - metrics.west) / 2.0
    half_d = (metrics.north - metrics.south) / 2.0
    radius = CORNER_RADIUS
    outer_tolerance = 0.01

    def on_outer_plan(vertex):
        qx = abs(vertex.co.x - centre_x) - (half_w - radius)
        qy = abs(vertex.co.y - centre_y) - (half_d - radius)
        outside = (
            math.hypot(max(qx, 0.0), max(qy, 0.0))
            + min(max(qx, qy), 0.0)
            - radius
        )
        return abs(outside) <= outer_tolerance

    bm = bmesh.new()
    bm.from_mesh(case.data)

    rim_edges = []
    for edge in bm.edges:
        if len(edge.link_faces) != 2:
            continue
        if not all(on_outer_plan(vertex) for vertex in edge.verts):
            continue
        if not any(abs(face.normal.z) < 0.1 for face in edge.link_faces):
            continue
        if not any(face.normal.z > 0.1 for face in edge.link_faces):
            continue
        rim_edges.append(edge)

    if rim_edges:
        bmesh.ops.bevel(
            bm,
            geom=rim_edges,
            offset=CHAMFER,
            offset_type="OFFSET",
            segments=1,
            profile=0.5,
            affect="EDGES",
            clamp_overlap=True,
        )

    bm.to_mesh(case.data)
    bm.free()
    case.data.update()


# --------------------------------------------------------------------------
# Cutters
# --------------------------------------------------------------------------


def _box_cutter(name, rect, z_min, z_max):
    bm = bmesh.new()
    mesh.add_box(
        bm,
        (rect.centre_x, rect.centre_y, (z_min + z_max) / 2.0),
        (rect.width, rect.height, z_max - z_min),
    )
    return mesh.mesh_object(name, bm)


def _rounded_rect_prism(name, rect, radius, z_min, z_max):
    """Solid with a rounded-rectangle plan, used for the split-case parts.

    The outer shell corners are rounded, so a rectangular lower recess would
    either break through them or become tangent at some height.  The recess
    and the bottom tray use the same rounded plan with a constant wall inset.
    """

    bm = bmesh.new()
    points = _rounded_rect_points(
        rect.centre_x,
        rect.centre_y,
        rect.width / 2.0,
        rect.height / 2.0,
        radius,
        CORNER_SEGMENTS,
    )
    mesh.add_prism_z(bm, points, z_min, z_max)
    return mesh.mesh_object(name, bm)


def _plate_pocket_cutter(metrics):
    return _box_cutter(
        "plate_pocket",
        metrics.plate_pocket,
        FLOOR,
        metrics.height + hardware.EPS,
    )


def _bay_cavity_cutter(metrics):
    """Clear the bay interior, opening its front corners to the lid ledge.

    The cover is L-shaped: its front wall is ``COVER_WALL`` thick and hangs
    down at the bay's south edge.  The lid ledge runs the full bay length, so
    without a notch the wall would collide with it and the cover could not
    seat.  The cavity is therefore widened to the bay opening over
    ``COVER_WALL + RAIL_FRONT_GAP + LID_LEDGE_SETBACK``, which ends the ledge
    2 mm further behind the cover lip.  The side magnet shelves stay at
    ``COVER_WALL + RAIL_FRONT_GAP``; their front ends are hidden under the
    cover.
    """

    opening = metrics.bay_opening
    cavity = metrics.bay_cavity
    notch = (
        opening.y0
        + COVER_WALL
        + mounting.RAIL_FRONT_GAP
        + LID_LEDGE_SETBACK
    )
    profile = (
        (opening.x0, opening.y0),
        (opening.x1, opening.y0),
        (opening.x1, notch),
        (cavity.x1, notch),
        (cavity.x1, cavity.y1),
        (cavity.x0, cavity.y1),
        (cavity.x0, notch),
        (opening.x0, notch),
    )

    bm = bmesh.new()
    mesh.add_prism_z(bm, profile, FLOOR, metrics.height + hardware.EPS)
    return mesh.mesh_object("bay_cavity", bm)


def _bay_opening_cutter(metrics):
    """Widen the top of the bay so the cover can sit on a flush shelf."""

    return _box_cutter(
        "bay_opening",
        metrics.bay_opening,
        metrics.shelf,
        metrics.height + hardware.EPS,
    )


def _compartment_ramp_cutter(metrics, ramp):
    """Trim the south part of the case down to the rim, slanted into the bay.

    The cutter's lower face is the case rim height in the south and ramps up
    to the compartment height at the bay's south edge, so the raised part has
    slanted edges instead of a vertical step.
    """

    y_end = metrics.bay_opening.y0
    y_start = max(y_end - ramp, metrics.south - CUTTER_MARGIN)
    x0 = metrics.west - CUTTER_MARGIN
    x1 = metrics.east + CUTTER_MARGIN
    top = metrics.height + CUTTER_HEIGHT
    far = metrics.south - CUTTER_MARGIN

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
    mesh.add_prism_x(bm, profile, x0, x1)
    return mesh.mesh_object("compartment_ramp", bm)


def _usb_profile(x, z, width, height, radius):
    return _rounded_rect_points(
        x, z, width / 2.0, height / 2.0, radius, CORNER_SEGMENTS
    )


def _usb_port_cutter(metrics):
    """Rounded through-opening for the USB-C plug."""

    x, z = metrics.usb_center
    profile = _usb_profile(
        x, z, USB_PORT_WIDTH, USB_PORT_HEIGHT, USB_PORT_RADIUS
    )
    bm = bmesh.new()
    mesh.add_prism_y(
        bm,
        profile,
        metrics.bay_cavity.y1 - USB_CUTTER_OVERLAP,
        metrics.north + USB_CUTTER_OVERLAP,
    )
    return mesh.mesh_object("usb_port", bm)


def _usb_bevel_cutter(metrics):
    """Beveled entrance and flat seat ring around the USB-C port.

    The bevel runs from the larger outer opening to the ring at depth.  The
    through cutter then removes the smaller port, leaving the flat ring as the
    bottom of the recess.
    """

    x, z = metrics.usb_center
    outer = _usb_profile(
        x, z, USB_RECESS_WIDTH, USB_RECESS_HEIGHT, USB_RECESS_RADIUS
    )
    ring = _usb_profile(
        x,
        z,
        USB_BEVEL_INNER_WIDTH,
        USB_BEVEL_INNER_HEIGHT,
        USB_BEVEL_INNER_RADIUS,
    )
    rings = (
        (metrics.north + USB_CUTTER_OVERLAP, outer),
        (metrics.north, outer),
        (metrics.north - USB_RECESS_DEPTH, ring),
    )
    bm = bmesh.new()
    _add_loft_y(bm, rings)
    return mesh.mesh_object("usb_bevel", bm)


def _usb_platform(metrics):
    """Shelf inside the bay, under the USB opening, for gluing the connector."""

    x, z = metrics.usb_center
    z_top = z - USB_PORT_HEIGHT / 2.0 + USB_PLATFORM_TOP_OVERLAP
    z_bottom = z_top - USB_PLATFORM_THICKNESS
    y0 = metrics.bay_cavity.y1 - USB_PLATFORM_DEPTH
    y1 = metrics.bay_cavity.y1 + USB_PLATFORM_EMBED

    bm = bmesh.new()
    mesh.add_box(
        bm,
        (x, (y0 + y1) / 2.0, (z_bottom + z_top) / 2.0),
        (
            USB_PLATFORM_WIDTH,
            y1 - y0,
            z_top - z_bottom,
        ),
    )
    return mesh.mesh_object("usb_platform", bm)


# --------------------------------------------------------------------------
# Wire routing and cover magnets
# --------------------------------------------------------------------------


def _wire_recess_cutter(metrics, options):
    """Recess the plate-pocket floor so wires can pass under the plate.

    The recess is inset slightly on the west, east and south walls so the
    bottom tray keeps a solid rim under the plate edge.  It stays open to the
    bay on the north side.
    """

    inset = WIRE_RECESS_INSET
    pocket = metrics.plate_pocket
    recess = layout.Rect(
        pocket.x0 + inset,
        pocket.x1 - inset,
        pocket.y0 + inset,
        pocket.y1,
    )
    return _box_cutter(
        "wire_recess",
        recess,
        FLOOR - options.wire_gap,
        FLOOR + hardware.EPS,
    )


def _floor_rib_builder(metrics, options):
    """Short support ribs, one in each gap between adjacent 1u keys."""

    bm = bmesh.new()
    for rect in metrics.ribs:
        mesh.add_box(
            bm,
            (rect.centre_x, rect.centre_y, FLOOR - options.wire_gap / 2.0),
            (rect.width, rect.height, options.wire_gap),
        )
    return mesh.mesh_object("floor_ribs", bm)


def _magnet_shelf_builders(metrics):
    """One small 45-degree shelf per magnet.

    Each shelf is a short pad around a single magnet pocket: a flat top, a
    45-degree underside running back to the cavity wall, and only enough length
    along the rail to leave a thin wall around the pocket.  There is no
    continuous rail.  The shelves attach to the cavity wall, so the sloped
    underside does not cross the bay wall.
    """

    builders = []

    for mount in metrics.magnets.mounts:
        x0, x1, y0, y1 = mount.rail
        top = mount.rail_top
        edge = mount.pocket_depth + MAGNET_SHELF_EDGE
        width = mount.shelf_width
        half = mount.shelf_length / 2.0
        bm = bmesh.new()
        if mount.side == "west":
            profile = (
                (x0, top),
                (x0 + width, top),
                (x0 + width, top - edge),
                (x0, top - edge - width),
            )
            mesh.add_prism_y(
                bm, profile, mount.centre[1] - half, mount.centre[1] + half
            )
        elif mount.side == "east":
            profile = (
                (x1, top),
                (x1 - width, top),
                (x1 - width, top - edge),
                (x1, top - edge - width),
            )
            mesh.add_prism_y(
                bm, profile, mount.centre[1] - half, mount.centre[1] + half
            )
        else:  # north
            profile = (
                (y1, top),
                (y1 - width, top),
                (y1 - width, top - edge),
                (y1, top - edge - width),
            )
            mesh.add_prism_x(
                bm, profile, mount.centre[0] - half, mount.centre[0] + half
            )
        builders.append(
            mesh.mesh_object(f"magnet_shelf_{len(builders)}", bm)
        )
    return builders


def _magnet_pocket_cutters(metrics):
    """Magnet pockets in the tops of the 45-degree case shelves."""

    bm = bmesh.new()
    for mount in metrics.magnets.mounts:
        cx, cy = mount.centre
        width, length = mount.size
        z0 = mount.rail_top - mount.pocket_depth
        z1 = mount.rail_top + hardware.EPS
        mesh.add_box(
            bm,
            (cx, cy, (z0 + z1) / 2.0),
            (width, length, z1 - z0),
        )
    return mesh.mesh_object("magnet_pockets", bm)


# --------------------------------------------------------------------------
# Split-case specific geometry
# --------------------------------------------------------------------------


def _edge_lower_cutter(metrics):
    """The internal recess that receives the bottom tray and plate tabs.

    It is straight-sided all the way to z = 0: the bottom tray and its lip can
    slide in from below.  The 1.5 mm outer bottom chamfer still leaves about
    1.05 mm of outer wall at the very bottom.
    """

    return _rounded_rect_prism(
        "edge_lower",
        metrics.edge_lower,
        EDGE_LOWER_RADIUS,
        -hardware.EPS,
        metrics.tab_top,
    )


def _bottom_bed_builder(metrics):
    """The 3 mm bottom tray bed, with a rounded plan."""

    return _rounded_rect_prism(
        "case_bottom_bed",
        metrics.bottom,
        BOTTOM_RADIUS,
        0.0,
        FLOOR,
    )


def _bottom_lip_builder(metrics):
    """Upstanding perimeter lip, interrupted at each plate tab."""

    outer = metrics.bottom
    inner = layout.Rect(
        outer.x0 + BOTTOM_LIP_WIDTH,
        outer.x1 - BOTTOM_LIP_WIDTH,
        outer.y0 + BOTTOM_LIP_WIDTH,
        outer.y1 - BOTTOM_LIP_WIDTH,
    )
    z0 = FLOOR - BOTTOM_LIP_OVERLAP
    z1 = metrics.tab_top - BOTTOM_LIP_CLEARANCE

    ring = _rounded_rect_prism(
        "case_bottom_lip",
        outer,
        BOTTOM_RADIUS,
        z0,
        z1,
    )
    inner_cutter = _rounded_rect_prism(
        "case_bottom_lip_inner",
        inner,
        BOTTOM_RADIUS - BOTTOM_LIP_WIDTH,
        z0 - 0.1,
        z1 + 0.5,
    )
    mesh.difference(ring, inner_cutter)

    gap_bm = bmesh.new()
    for tab in metrics.tabs.tabs:
        if tab.side == "west":
            x0, x1 = outer.x0 - 0.5, outer.x0 + BOTTOM_LIP_WIDTH + 0.5
            y0 = tab.centre[1] - mounting.TAB_WIDTH / 2.0 - BOTTOM_LIP_TAB_CLEARANCE
            y1 = tab.centre[1] + mounting.TAB_WIDTH / 2.0 + BOTTOM_LIP_TAB_CLEARANCE
        elif tab.side == "east":
            x0, x1 = outer.x1 - BOTTOM_LIP_WIDTH - 0.5, outer.x1 + 0.5
            y0 = tab.centre[1] - mounting.TAB_WIDTH / 2.0 - BOTTOM_LIP_TAB_CLEARANCE
            y1 = tab.centre[1] + mounting.TAB_WIDTH / 2.0 + BOTTOM_LIP_TAB_CLEARANCE
        else:
            x0 = tab.centre[0] - mounting.TAB_WIDTH / 2.0 - BOTTOM_LIP_TAB_CLEARANCE
            x1 = tab.centre[0] + mounting.TAB_WIDTH / 2.0 + BOTTOM_LIP_TAB_CLEARANCE
            y0, y1 = outer.y0 - 0.5, outer.y0 + BOTTOM_LIP_WIDTH + 0.5
        mesh.add_box(
            gap_bm,
            ((x0 + x1) / 2.0, (y0 + y1) / 2.0, (FLOOR - 0.05 + z1 + 0.5) / 2.0),
            (x1 - x0, y1 - y0, z1 + 0.55 - (FLOOR - 0.05)),
        )
    if metrics.tabs.tabs:
        gap_cutter = mesh.mesh_object("case_bottom_lip_gaps", gap_bm)
        mesh.difference(ring, gap_cutter)
    else:
        gap_bm.free()

    return ring


def _countersunk_holes_cutter(name, centres, through_top):
    """3 mm through-holes with a 90-degree countersink from the bottom face."""

    shaft_radius = hardware.SCREW_HOLE_DIAMETER / 2.0
    head_radius = hardware.SCREW_COUNTERSINK_DIAMETER / 2.0
    depth = hardware.SCREW_COUNTERSINK_DEPTH
    profile = (
        (head_radius + 0.5, -0.5),
        (shaft_radius, depth),
        (shaft_radius, through_top),
    )
    bm = bmesh.new()
    for cx, cy in centres:
        mesh.add_revolved_profile(bm, (cx, cy), profile)
    return mesh.mesh_object(name, bm)


def _edge_box_rects(metrics):
    """Plan rectangles of the four screw boxes on the north wall."""

    y_south = metrics.bay_cavity.y1
    y_north = metrics.edge_lower.y1 + EDGE_BOX_EMBED
    half = EDGE_BOX_WIDTH / 2.0
    return tuple(
        layout.Rect(x - half, x + half, y_south, y_north)
        for x, _y in metrics.edge_screws
    )


def _edge_box_builders(metrics):
    """Four boxes that thicken the thin lower north wall around the screws."""

    builders = []
    for index, rect in enumerate(_edge_box_rects(metrics)):
        bm = bmesh.new()
        mesh.add_box(
            bm,
            (
                rect.centre_x,
                rect.centre_y,
                (FLOOR + EDGE_BOX_TOP) / 2.0,
            ),
            (rect.width, rect.height, EDGE_BOX_TOP - FLOOR),
        )
        builders.append(mesh.mesh_object(f"edge_screw_box_{index}", bm))
    return builders


def _edge_box_pilot_cutter(metrics):
    """Blind 3 mm pilot holes through the four north-wall boxes."""

    radius = hardware.SCREW_HOLE_DIAMETER / 2.0
    bm = bmesh.new()
    for x, y in metrics.edge_screws:
        mesh.add_cylinder(
            bm,
            (x, y),
            radius,
            FLOOR - 0.5,
            FLOOR + EDGE_BOX_SCREW_DEPTH,
        )
    return mesh.mesh_object("edge_screw_pilots", bm)


def _edge_box_lip_cutters(metrics):
    """Clear the tray lip where each north-wall box drops down onto it."""

    bm = bmesh.new()
    for rect in _edge_box_rects(metrics):
        mesh.add_box(
            bm,
            (
                rect.centre_x,
                rect.centre_y,
                (FLOOR - 0.1 + metrics.tab_top + 0.5) / 2.0,
            ),
            (
                rect.width + 2.0 * EDGE_BOX_LIP_CLEARANCE,
                rect.height + 2.0 * EDGE_BOX_LIP_CLEARANCE,
                metrics.tab_top + 0.5 - (FLOOR - 0.1),
            ),
        )
    return mesh.mesh_object("edge_box_lip_cutouts", bm)


def _front_wedge_builder(metrics):
    """Wide triangular ledge on the south wall, minus the plate-tab notches."""

    wall_y = metrics.edge_lower.y0
    x0 = -FRONT_WEDGE_WIDTH_FRACTION * metrics.width / 2.0
    x1 = FRONT_WEDGE_WIDTH_FRACTION * metrics.width / 2.0
    profile = (
        (wall_y - FRONT_WEDGE_EMBED, FRONT_WEDGE_BOTTOM),
        (wall_y + FRONT_WEDGE_DEPTH, FRONT_WEDGE_BOTTOM),
        (wall_y, FRONT_WEDGE_TOP),
        (wall_y - FRONT_WEDGE_EMBED, FRONT_WEDGE_TOP),
    )
    bm = bmesh.new()
    mesh.add_prism_x(bm, profile, x0, x1)
    wedge = mesh.mesh_object("front_wedge", bm)

    notches = bmesh.new()
    for tab in metrics.tabs.by_side("south"):
        nx0 = tab.centre[0] - mounting.TAB_WIDTH / 2.0 - BOTTOM_LIP_TAB_CLEARANCE
        nx1 = tab.centre[0] + mounting.TAB_WIDTH / 2.0 + BOTTOM_LIP_TAB_CLEARANCE
        ny0 = wall_y - 0.1
        ny1 = tab.centre[1] + mounting.TAB_DEPTH / 2.0 + BOTTOM_LIP_TAB_CLEARANCE
        mesh.add_box(
            notches,
            (
                (nx0 + nx1) / 2.0,
                (ny0 + ny1) / 2.0,
                (FRONT_WEDGE_BOTTOM - 1.0 + FRONT_WEDGE_TOP + 1.0) / 2.0,
            ),
            (
                nx1 - nx0,
                ny1 - ny0,
                FRONT_WEDGE_HEIGHT + 2.0,
            ),
        )
    if metrics.tabs.by_side("south"):
        mesh.difference(wedge, mesh.mesh_object("front_wedge_notches", notches))
    else:
        notches.free()
    return wedge


def _front_wedge_dent_cutter(metrics):
    """Triangular dent in the tray's south side, matching the front ledge.

    The cutter is the same right triangle as the ledge, enlarged by a uniform
    clearance and extruded over the same width, so the two profiles match.
    """

    wall_y = metrics.edge_lower.y0
    bottom = FRONT_WEDGE_BOTTOM
    top = FRONT_WEDGE_TOP
    clearance = FRONT_WEDGE_CLEARANCE
    out_y = wall_y - clearance
    profile = (
        (out_y, bottom - clearance),
        (wall_y + FRONT_WEDGE_DEPTH + clearance, bottom - clearance),
        (out_y, top + clearance),
    )
    half = (
        FRONT_WEDGE_WIDTH_FRACTION * metrics.width / 2.0
        + clearance
    )
    bm = bmesh.new()
    mesh.add_prism_x(bm, profile, -half, half)
    return mesh.mesh_object("front_wedge_dent", bm)


# --------------------------------------------------------------------------
# Part generation
# --------------------------------------------------------------------------


def build_case_bottom(
    keys,
    params=None,
    name="case_bottom",
    compartment_height=None,
    compartment_ramp=None,
    wall=None,
    compartment_depth=None,
    options=None,
):
    """Build the inset bottom tray with wire recess and ribs."""

    ramp = COMPARTMENT_RAMP if compartment_ramp is None else compartment_ramp
    if not math.isfinite(ramp) or ramp < 0.0:
        raise ValueError(
            f"compartment_ramp must be a non-negative number, got {ramp}"
        )
    options = options or mounting.MountingOptions()
    metrics = case_metrics(
        keys,
        params,
        compartment_height=compartment_height,
        wall=wall,
        compartment_depth=compartment_depth,
        options=options,
    )

    bottom = _bottom_bed_builder(metrics)
    mesh.union(bottom, _bottom_lip_builder(metrics))

    mesh.difference(bottom, _wire_recess_cutter(metrics, options))
    if metrics.ribs:
        mesh.union(bottom, _floor_rib_builder(metrics, options))

    # New screw mounting: two pilot-screw holes through the bed into the plate,
    # four north-edge holes into the edge-ring boxes, and the front triangular
    # interlock notch.
    mesh.difference(bottom, _edge_box_lip_cutters(metrics))
    mesh.difference(bottom, _front_wedge_dent_cutter(metrics))
    mesh.difference(
        bottom,
        _countersunk_holes_cutter(
            "plate_screw_holes", metrics.plate_screws, FLOOR + hardware.EPS
        ),
    )
    mesh.difference(
        bottom,
        _countersunk_holes_cutter(
            "edge_screw_holes", metrics.edge_screws, FLOOR + hardware.EPS
        ),
    )

    mesh.check_mesh(name, bottom, strict=True)
    return bottom


# --------------------------------------------------------------------------
# Bottom-tray print split
# --------------------------------------------------------------------------


def should_split_bottom(metrics):
    """A bottom tray above the split threshold is printed as two halves."""

    return metrics.width > BOTTOM_SPLIT_MIN_WIDTH


def bottom_piece_path(path, suffix):
    """Derive ``_west`` / ``_east`` paths from the normal bottom-tray path."""

    stem, extension = os.path.splitext(path)
    extension = extension or ".stl"
    return f"{stem}_{suffix}{extension}"


def _bottom_dovetail_positions(metrics):
    """Y centres of the dovetails along the central X = 0 seam.

    The tenons stay inside the middle ``BOTTOM_DOVETAIL_SPAN_FRACTION`` of the
    plate depth, so they sit in the recessed wire floor and keep clear of the
    perimeter lip at the north and south ends of the seam.
    """

    pocket = metrics.plate_pocket
    depth = pocket.y1 - pocket.y0
    span = depth * BOTTOM_DOVETAIL_SPAN_FRACTION
    count = max(
        2,
        int(span / BOTTOM_DOVETAIL_TARGET_SPACING + 0.5) + 1,
    )
    count = min(count, 6)
    spacing = span / (count - 1)
    minimum = BOTTOM_DOVETAIL_TIP + 2.0 * BOTTOM_DOVETAIL_MIN_WEB
    if spacing < minimum - 1e-9:
        raise ValueError(
            "the bottom tray is too shallow for multiple dovetails: "
            f"needs {minimum:.1f} mm spacing, has {spacing:.1f} mm"
        )

    centre = (pocket.y0 + pocket.y1) / 2.0
    positions = [
        centre + (index - (count - 1) / 2.0) * spacing
        for index in range(count)
    ]

    recess_y0 = pocket.y0 + WIRE_RECESS_INSET
    if positions[0] - BOTTOM_DOVETAIL_TIP / 2.0 < recess_y0 - 1e-9:
        raise ValueError(
            "the bottom dovetails would leave the recessed wire floor"
        )
    return positions


def _box_between(name, x0, x1, y0, y1, z0, z1):
    """Axis-aligned box solid from its extents."""

    bm = bmesh.new()
    mesh.add_box(
        bm,
        ((x0 + x1) / 2.0, (y0 + y1) / 2.0, (z0 + z1) / 2.0),
        (x1 - x0, y1 - y0, z1 - z0),
    )
    return mesh.mesh_object(name, bm)


def _dovetail_prism(name, x_root, x_tip, y_centre, half_root, half_tip, z0, z1):
    """A dovetail trapezoid extruded vertically."""

    points = (
        (x_root, y_centre - half_root),
        (x_root, y_centre + half_root),
        (x_tip, y_centre + half_tip),
        (x_tip, y_centre - half_tip),
    )
    bm = bmesh.new()
    mesh.add_prism_z(bm, points, z0, z1)
    return mesh.mesh_object(name, bm)


def _x_extent(obj):
    xs = [vertex.co.x for vertex in obj.data.vertices]
    return max(xs) - min(xs)


def split_case_bottom(
    bottom,
    metrics,
    name="case_bottom",
    options=None,
):
    """Cut a built bottom tray into west/east dovetailed halves.

    One straight cut at X = 0 separates the tray; several dovetail tenons are
    then added to the west half and matching slots cut into the east half.  The
    tenons are short in Z because the seam runs through the recessed wire floor,
    and they are prismatic in Z, so the east half drops onto them during
    assembly.  Returns ``[("west", object), ("east", object)]``.  The source
    ``bottom`` object is left untouched.
    """

    if not should_split_bottom(metrics):
        raise ValueError(
            f"{name} is only {metrics.width:.1f} mm wide; the print split is "
            f"for cases wider than {BOTTOM_SPLIT_MIN_WIDTH:.0f} mm"
        )
    options = options or mounting.MountingOptions()
    floor_top = FLOOR - options.wire_gap
    half = BOTTOM_SEAM_GAP / 2.0
    big = max(
        abs(metrics.west),
        abs(metrics.east),
        abs(metrics.south),
        abs(metrics.north),
    ) + 50.0

    positions = _bottom_dovetail_positions(metrics)
    print(
        f"{name}: splitting into west/east halves at x = 0 with "
        f"{len(positions)} dovetails"
    )

    west = mesh.duplicate_object(bottom, f"{name}_west")
    east = mesh.duplicate_object(bottom, f"{name}_east")
    mesh.difference(
        west, _box_between(f"{name}_west_cut", -half, big, -big, big, -big, big)
    )
    mesh.difference(
        east, _box_between(f"{name}_east_cut", -big, half, -big, big, -big, big)
    )

    tenon_root = -half - 0.5
    tenon_tip = half + BOTTOM_TENON_DEPTH
    slot_mouth = half - 0.5
    slot_end = half + BOTTOM_DOVETAIL_DEPTH

    def slot_half_at(x):
        t = (x - slot_mouth) / (slot_end - slot_mouth)
        return (
            BOTTOM_DOVETAIL_NECK / 2.0
            + t * (BOTTOM_DOVETAIL_TIP - BOTTOM_DOVETAIL_NECK) / 2.0
        )

    for index, y_centre in enumerate(positions):
        tenon = _dovetail_prism(
            f"{name}_tenon_{index}",
            tenon_root,
            tenon_tip,
            y_centre,
            slot_half_at(tenon_root) - BOTTOM_DOVETAIL_CLEARANCE,
            slot_half_at(tenon_tip) - BOTTOM_DOVETAIL_CLEARANCE,
            0.0,
            floor_top,
        )
        mesh.union(west, tenon)

        slot = _dovetail_prism(
            f"{name}_slot_{index}",
            slot_mouth,
            slot_end,
            y_centre,
            slot_half_at(slot_mouth),
            slot_half_at(slot_end),
            -0.1,
            floor_top + 0.1,
        )
        mesh.difference(east, slot)

    pieces = [("west", west), ("east", east)]
    for suffix, piece in pieces:
        mesh.check_mesh(f"{name}_{suffix}", piece, strict=True)
        extent = _x_extent(piece)
        if extent > BOTTOM_SPLIT_MAX_WIDTH + 1e-6:
            raise ValueError(
                f"{name}_{suffix} is {extent:.1f} mm wide, wider than the "
                f"{BOTTOM_SPLIT_MAX_WIDTH:.0f} mm target; the two-piece split "
                "does not make this case printable"
            )
    print(
        f"{name}: two pieces, widest "
        f"{max(_x_extent(piece) for _, piece in pieces):.2f} mm"
    )
    return pieces


def build_case_edges(
    keys,
    params=None,
    name="case_edges",
    compartment_height=None,
    compartment_ramp=None,
    wall=None,
    compartment_depth=None,
    options=None,
):
    """Build the full-height edge ring, bay, USB-C and magnet shelves."""

    ramp = COMPARTMENT_RAMP if compartment_ramp is None else compartment_ramp
    if not math.isfinite(ramp) or ramp < 0.0:
        raise ValueError(
            f"compartment_ramp must be a non-negative number, got {ramp}"
        )
    options = options or mounting.MountingOptions()
    metrics = case_metrics(
        keys,
        params,
        compartment_height=compartment_height,
        wall=wall,
        compartment_depth=compartment_depth,
        options=options,
    )

    bm = bmesh.new()
    _add_case_shell(bm, metrics)
    case = mesh.mesh_object(name, bm)

    if metrics.height > CASE_HEIGHT:
        mesh.difference(case, _compartment_ramp_cutter(metrics, ramp))
    _bevel_top_rim(case, metrics)

    cutters = [
        _plate_pocket_cutter(metrics),
        _bay_cavity_cutter(metrics),
        _bay_opening_cutter(metrics),
        _edge_lower_cutter(metrics),
    ]

    for cutter in cutters:
        mesh.difference(case, cutter)

    platform = _usb_platform(metrics)
    mesh.union(case, platform)
    for shelf in _magnet_shelf_builders(metrics):
        mesh.union(case, shelf)

    mesh.difference(case, _magnet_pocket_cutters(metrics))

    for cutter in (_usb_port_cutter(metrics), _usb_bevel_cutter(metrics)):
        mesh.difference(case, cutter)

    for box in _edge_box_builders(metrics):
        mesh.union(case, box)
    mesh.union(case, _front_wedge_builder(metrics))
    mesh.difference(case, _edge_box_pilot_cutter(metrics))

    mesh.check_mesh(name, case, strict=True)
    return case
