"""Custom 2 mm-wire stabilizers shared by the flat keyboards.

Design
------

Each stabilized key gets two square post holes and one U-shaped wire channel
cut into the bottom of the top plate.  The printed part is a symmetric slider:

* a 5 mm square post that passes through a 5.4 mm square plate hole,
* a standard MX cross on top (4.0 x 1.1 mm) at the switch-stem height,
* a 1.2 mm bottom floor with a slanted 2.4 mm wire hole leading into an
  internal capture slot.

The wire is a flat U: two short horizontal legs run north from the posts and
a straight span joins them.  The wire sits in the plate channel and passes
through the internal slot in each slider; the case floor traps the wire.  The
sliders ride vertically on the fixed wire; the keycap drives both sliders.
The wire span is offset north of the switch to clear the hotswap socket and
LED features.  The slanted bottom hole lets the wire end be fed into the slot
during assembly; the slider bottom itself is a closed face with a hole, not
an open cut.

All dimensions are millimetres.  The plate cutouts and the printed slider are
generated from the same constants, so the wire spacing in the plate and the
wire shown by the standalone generator always agree.
"""

from __future__ import annotations

import math
from dataclasses import dataclass

import bmesh

import hardware
import layout
import mesh

# --------------------------------------------------------------------------
# Wire and channel
# --------------------------------------------------------------------------

WIRE_DIAMETER = 2.0
WIRE_CLEARANCE = 0.2
WIRE_BEND_RADIUS = 1.7
WIRE_HOLE_DIAMETER = WIRE_DIAMETER + 2.0 * WIRE_CLEARANCE
SPAN_OFFSET_Y = 9.5          # north of the switch centre
CHANNEL_WIDTH = 3.8
BOTTOM_FLOOR = 1.2           # solid floor under the internal capture slot
POST_BOTTOM_Z = 4.0          # above the plate bottom, at rest
POST_TRAVEL = 4.0
WIRE_Z = POST_BOTTOM_Z + BOTTOM_FLOOR + WIRE_DIAMETER / 2.0
CHANNEL_DEPTH = WIRE_Z + WIRE_DIAMETER / 2.0 + WIRE_CLEARANCE

# --------------------------------------------------------------------------
# Slider
# --------------------------------------------------------------------------

POST_SIZE = 5.0
POST_HALF = POST_SIZE / 2.0
POST_HOLE = 5.4
SLOT_WIDTH = WIRE_HOLE_DIAMETER + 0.2
SLOT_BACK = -1.0             # closed south end of the internal slot
SLOT_BOTTOM = BOTTOM_FLOOR
SLOT_TOP = SLOT_BOTTOM + POST_TRAVEL + WIRE_DIAMETER + WIRE_CLEARANCE
KEYCAP_UNDERSIDE = 5.0       # above the plate top
STEM_TOP = 11.6              # MX stem top above the plate top
PLUS_OVERLAP = 0.3           # embed the cross in the body for a clean union

BODY_TOP_Z = hardware.PLATE_THICKNESS + KEYCAP_UNDERSIDE
STEM_TOP_Z = hardware.PLATE_THICKNESS + STEM_TOP
PLUS_HEIGHT = STEM_TOP_Z - BODY_TOP_Z

STEM_CROSS_LENGTH = 4.0
STEM_CROSS_WIDTH = 1.1

STAB_MIN_WIDTH = 2.0 * layout.PITCH
STAB_SMALL_OFFSET = 11.93    # Cherry 2u/2.25u/2.75u spacing is 23.86 mm

STAB_HORIZONTAL = "horizontal"
STAB_VERTICAL = "vertical"

# --------------------------------------------------------------------------
# Spacing and wire specifications
# --------------------------------------------------------------------------


@dataclass(frozen=True)
class WireSpec:
    """Geometry of one stabilizer wire."""

    key_width: float
    offset: float
    span: float
    leg: float
    bend_radius: float
    length: float


def stabilizer_orientation(key_width, key_height):
    """Return the stabilizer axis for a key, or None.

    Wide keys use a horizontal stabilizer.  A 1u-wide, 2u-tall key such as a
    numpad + or Enter uses the same part rotated 90 degrees.
    """

    if key_width >= 2.0 * layout.PITCH and key_width >= key_height:
        return STAB_HORIZONTAL
    if key_height >= 2.0 * layout.PITCH:
        return STAB_VERTICAL
    return None


def wire_offset(key_width):
    """Return the stabilizer centre offset from the switch centre, or None."""

    units = key_width / layout.PITCH
    if units < 2.0 - 1e-9:
        return None
    if units < 3.0 - 1e-9:
        return STAB_SMALL_OFFSET
    return (units - 1.0) * layout.PITCH / 2.0


def is_stabilized(key_width, key_height=None):
    """Whether a key of the given size needs stabilizers."""

    if key_height is None:
        return wire_offset(key_width) is not None
    return stabilizer_orientation(key_width, key_height) is not None


def wire_spec(key_width):
    """Return the wire geometry for a stabilized key width."""

    offset = wire_offset(key_width)
    if offset is None:
        raise ValueError(f"{key_width:.2f} mm is narrower than 2u")
    span = 2.0 * offset
    length = 2.0 * SPAN_OFFSET_Y + span + math.pi * WIRE_BEND_RADIUS
    return WireSpec(
        key_width=key_width,
        offset=offset,
        span=span,
        leg=SPAN_OFFSET_Y,
        bend_radius=WIRE_BEND_RADIUS,
        length=length,
    )


# --------------------------------------------------------------------------
# Plate cutouts
# --------------------------------------------------------------------------


def _rotate(offset, angle):
    c = math.cos(angle)
    s = math.sin(angle)
    return (offset[0] * c - offset[1] * s, offset[0] * s + offset[1] * c)


def _channel_profile(offset):
    """Closed U-shaped strip profile in key-local XY coordinates."""

    half = CHANNEL_WIDTH / 2.0
    span = SPAN_OFFSET_Y
    return [
        (-offset - half, 0.0),
        (-offset - half, span + half),
        (offset + half, span + half),
        (offset + half, 0.0),
        (offset - half, 0.0),
        (offset - half, span - half),
        (-offset + half, span - half),
        (-offset + half, 0.0),
    ]


def _transform(points, x, y, angle):
    c = math.cos(angle)
    s = math.sin(angle)
    return [
        (x + px * c - py * s, y + px * s + py * c) for px, py in points
    ]


def plate_cutters(placed, thickness):
    """Return the plate cutters for all stabilized keys.

    ``placed`` entries are ``(x, y, rotation, width, height)`` in plate
    coordinates.  Wide keys use a horizontal stabilizer; tall 1u keys use the
    same geometry rotated 90 degrees so the wire runs east into the margin.
    Two cutter objects are returned: all square post holes and all wire
    channels.  They are subtracted separately because they overlap.
    """

    holes = []
    channels = []
    for x, y, rotation, width, height in placed:
        orientation = stabilizer_orientation(width, height)
        if orientation is None:
            continue

        dimension = width if orientation == STAB_HORIZONTAL else height
        offset = wire_offset(dimension)
        local_rotation = rotation
        if orientation == STAB_VERTICAL:
            local_rotation -= math.pi / 2.0

        for sign in (-1.0, 1.0):
            dx, dy = _rotate((sign * offset, 0.0), local_rotation)
            holes.append((x + dx, y + dy, local_rotation))

        channels.append(
            _transform(_channel_profile(offset), x, y, local_rotation)
        )

    if not holes:
        return []

    holes_bm = bmesh.new()
    for cx, cy, rotation in holes:
        mesh.add_box(
            holes_bm,
            (cx, cy, thickness / 2.0),
            (POST_HOLE, POST_HOLE, thickness + 2.0 * hardware.EPS),
            rotation,
        )
    holes_obj = mesh.mesh_object("stabilizer_holes", holes_bm)

    channels_bm = bmesh.new()
    for profile in channels:
        mesh.add_prism_z(
            channels_bm,
            profile,
            -hardware.EPS,
            CHANNEL_DEPTH,
        )
    channels_obj = mesh.mesh_object("stabilizer_channels", channels_bm)

    return [holes_obj, channels_obj]


# --------------------------------------------------------------------------
# Printed slider
# --------------------------------------------------------------------------


def _cross_profile():
    """Closed MX cross profile in XY coordinates."""

    half_l = STEM_CROSS_LENGTH / 2.0
    half_w = STEM_CROSS_WIDTH / 2.0
    return [
        (-half_w, -half_l),
        (half_w, -half_l),
        (half_w, -half_w),
        (half_l, -half_w),
        (half_l, half_w),
        (half_w, half_w),
        (half_w, half_l),
        (-half_w, half_l),
        (-half_w, half_w),
        (-half_l, half_w),
        (-half_l, -half_w),
        (-half_w, -half_w),
    ]


def _slot_profile():
    """Closed X/Z profile for the internal wire capture slot."""

    return [
        (-SLOT_WIDTH / 2.0, SLOT_BOTTOM),
        (SLOT_WIDTH / 2.0, SLOT_BOTTOM),
        (SLOT_WIDTH / 2.0, SLOT_TOP),
        (-SLOT_WIDTH / 2.0, SLOT_TOP),
    ]


def build_slider(name="stabilizer_slider"):
    """Build one symmetric slider with its bottom at z = 0.

    The bottom is a solid floor with a slanted 2.4 mm wire hole leading into
    the internal capture slot.  Print two per stabilized key; the post is
    symmetric, so the same STL is used on both sides of the key.
    """

    body_top = BODY_TOP_Z - POST_BOTTOM_Z
    stem_top = STEM_TOP_Z - POST_BOTTOM_Z

    body_bm = bmesh.new()
    mesh.add_box(
        body_bm,
        (0.0, 0.0, body_top / 2.0),
        (POST_SIZE, POST_SIZE, body_top),
    )
    slider = mesh.mesh_object(name, body_bm)

    plus_bm = bmesh.new()
    mesh.add_prism_z(
        plus_bm,
        _cross_profile(),
        body_top - PLUS_OVERLAP,
        stem_top,
    )
    plus = mesh.mesh_object(f"{name}_stem", plus_bm)
    mesh.union(slider, plus)

    slot_bm = bmesh.new()
    mesh.add_prism_y(
        slot_bm,
        _slot_profile(),
        SLOT_BACK,
        POST_HALF + 0.25,
    )
    slot = mesh.mesh_object(f"{name}_slot", slot_bm)
    mesh.difference(slider, slot)

    hole_bm = bmesh.new()
    mesh.add_cylinder_between(
        hole_bm,
        (0.0, -BOTTOM_FLOOR, -0.2),
        (0.0, 0.2, BOTTOM_FLOOR + 0.2),
        WIRE_HOLE_DIAMETER / 2.0,
    )
    hole = mesh.mesh_object(f"{name}_hole", hole_bm)
    mesh.difference(slider, hole)

    mesh.check_mesh(name, slider, strict=True)
    return slider
