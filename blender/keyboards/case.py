"""Flat keyboard case with a top electronics bay.

The case is a hollow tray with 45 degree chamfers on the top and bottom rims
and rounded corners.  The top plate drops into a pocket that the case wall
wraps on the left, right and bottom (south) sides.  On the top (north) side the
case extends into an electronics bay for the controller and batteries.

``DEFAULT_COMPARTMENT_HEIGHT`` sets the top of that bay (default: the case
rim, i.e. not raised).  When it is higher than the case rim, the case's outer
edges are slanted over ``COMPARTMENT_RAMP`` from the rim height up to the
compartment height.

This module builds the case only.  The separate cover lives in
:mod:`cover`; both share :func:`case_metrics` so they always fit the same bay.
All dimensions are millimetres; one Blender unit is one millimetre on STL
export.
"""

from __future__ import annotations

import math
from dataclasses import dataclass

import bmesh

import hardware
import layout
import mesh

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

PLATE_TOP = FLOOR + hardware.PLATE_THICKNESS
CASE_HEIGHT = PLATE_TOP + LID_THICKNESS

DEFAULT_COMPARTMENT_HEIGHT = CASE_HEIGHT
COMPARTMENT_HEIGHT = DEFAULT_COMPARTMENT_HEIGHT  # compatibility name
COMPARTMENT_RAMP = 20.0    # horizontal run of the slanted edge
COVER_WALL = 2.5           # cover front wall thickness

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


def case_metrics(keys, params=None, compartment_height=None):
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

    params = params or layout.PlateParams()
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

    west = pocket.x0 - WALL
    east = pocket.x1 + WALL
    south = pocket.y0 - WALL

    # The bay starts at the plate pocket's north edge, so the plate is wrapped
    # by the case on three sides only; the cover's front wall closes the bay
    # against the top row of keys.
    bay_south = pocket.y1
    bay_north = bay_south + COMPARTMENT_DEPTH
    north = bay_north + WALL

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
    bm = bmesh.new()
    mesh.add_box(
        bm,
        (rect.centre_x, rect.centre_y, (z_min + z_max) / 2.0),
        (rect.width, rect.height, z_max - z_min),
    )
    return mesh.mesh_object(name, bm)


def _plate_pocket_cutter(metrics):
    return _box_cutter(
        "plate_pocket",
        metrics.plate_pocket,
        FLOOR,
        metrics.height + hardware.EPS,
    )


def _bay_cavity_cutter(metrics):
    return _box_cutter(
        "bay_cavity",
        metrics.bay_cavity,
        FLOOR,
        metrics.height + hardware.EPS,
    )


def _bay_opening_cutter(metrics):
    """Widen the top of the bay so the cover can sit on a flush shelf."""

    return _box_cutter(
        "bay_opening",
        metrics.bay_opening,
        metrics.height - LID_THICKNESS,
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


def _usb_port_cutter(metrics):
    x, z = metrics.usb_center
    rect = layout.Rect(
        x - USB_PORT_WIDTH / 2.0,
        x + USB_PORT_WIDTH / 2.0,
        metrics.bay_cavity.y1 - USB_CUTTER_OVERLAP,
        metrics.north + USB_CUTTER_OVERLAP,
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


def build_case(
    keys,
    params=None,
    name="case",
    compartment_height=None,
    compartment_ramp=None,
):
    """Build the case; the plate pocket is centred like the top plate."""

    ramp = COMPARTMENT_RAMP if compartment_ramp is None else compartment_ramp
    if not math.isfinite(ramp) or ramp < 0.0:
        raise ValueError(
            f"compartment_ramp must be a non-negative number, got {ramp}"
        )
    metrics = case_metrics(keys, params, compartment_height=compartment_height)

    bm = bmesh.new()
    _add_case_shell(bm, metrics)
    case = mesh.mesh_object(name, bm)

    cutters = [
        _plate_pocket_cutter(metrics),
        _bay_cavity_cutter(metrics),
        _bay_opening_cutter(metrics),
        _usb_port_cutter(metrics),
    ]
    if metrics.height > CASE_HEIGHT:
        cutters.insert(0, _compartment_ramp_cutter(metrics, ramp))

    for cutter in cutters:
        mesh.difference(case, cutter)

    mesh.check_mesh(name, case, strict=True)
    return case
