"""Shared MX switch, hotswap socket and RGB LED hardware cutouts.

These are the parts that every keyboard type has in common: a stepped 5-pin
MX switch pocket, a Gateron/Kailh hotswap socket pocket, a WS2812B LED pocket
and light hole, and the centre-post/pin/leg through holes.  Cases, split
halves and curved Dactyl key wells can all reuse these dimensions and cutter
builders.

The cutters here are built in the plate's local frame, with the switch centre
at ``(x, y)`` and the plate bottom at ``z = 0``.  For a flat board the only
placement is a Z rotation; a curved Dactyl builder will need to apply its own
3D transform to the same local geometry.
"""

from __future__ import annotations

import math

import bmesh

import mesh

# --------------------------------------------------------------------------
# MX switch (5 pin), seen from the switch centre; +y points "north"
# --------------------------------------------------------------------------

MX_POCKET = 14.0                 # switch pocket band at the plate surface
MX_POCKET_CLIP_X = 14.0          # clip relief below the band, X direction
MX_POCKET_CLIP_Y = 15.0          # clip relief below the band, Y direction
CLIP_RELIEF_DEPTH = 1.0          # 14x14 band depth before the clips expand
MX_POCKET_DEPTH = 5.0            # encloses the whole lower switch housing
MX_CENTER_POST_DIAMETER = 4.2    # centre post is 4.0 mm
# Hotswap socket footprint, seen from the plate bottom.  The two contact
# holes are mirrored on X relative to the switch's top-view pin layout; the
# X values are swapped here so the socket pattern matches the physical pads.
MX_PIN_POSITIONS = (             # the two hotswap contact holes
    (3.81, -2.54),
    (-2.54, -5.08),
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

SOCKET_POCKET_LENGTH = 12.0      # X, known-good body envelope
SOCKET_POCKET_WIDTH = 9.0        # Y, known-good body envelope
SOCKET_CONTACT_SIZE = 2.0        # protruding metal contact tab
SOCKET_CONTACT_EMBED = 0.2       # overlap into the body pocket
SOCKET_POCKET_DEPTH = 3.0

# --------------------------------------------------------------------------
# WS2812B RGB LED (5050 package)
# --------------------------------------------------------------------------

LED_POCKET = 5.2                 # 5.0 mm package plus clearance
LED_POCKET_DEPTH = 1.7
LED_LIGHT_HOLE_DIAMETER = 3.6    # lets the LED shine through the plate
LED_OFFSET_Y = 5.08              # north of the switch centre
LED_OFFSET = (0.0, LED_OFFSET_Y)

# --------------------------------------------------------------------------
# Derived thickness and cutter overlap
# --------------------------------------------------------------------------

WEB_THICKNESS = 1.5              # solid floor between switch and socket/LED
PLATE_THICKNESS = MX_POCKET_DEPTH + WEB_THICKNESS + SOCKET_POCKET_DEPTH
EPS = 0.5                        # cutters overshoot the plate faces

# The hotswap body pocket is the proven 12 x 9 mm envelope.  Only the higher
# pin gets a side extension for the protruding metal tab; the lower pin already
# clears the original pocket.
_SOCKET_MID_OFFSET = (-0.635, -3.81)


def _rotate(offset, angle):
    """Rotate a 2D offset by ``angle`` radians."""

    c = math.cos(angle)
    s = math.sin(angle)
    return (offset[0] * c - offset[1] * s, offset[0] * s + offset[1] * c)


def add_switch_pocket(bm, x, y, rotation, thickness):
    """Add one stepped MX pocket to ``bm``.

    The closed XZ profile is extruded along Y: a 14 x 14 mm band at the
    surface, opened to 15 x 14 mm one millimetre below it so the horizontal
    switch clips can spring back out.
    """

    x_inner = MX_POCKET / 2.0
    x_outer = MX_POCKET_CLIP_X / 2.0
    y_half = MX_POCKET_CLIP_Y / 2.0
    z_bottom = thickness - MX_POCKET_DEPTH
    z_step = thickness - CLIP_RELIEF_DEPTH
    z_top = thickness + EPS

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

    bm.faces.new(tuple(rings[0]))
    bm.faces.new(tuple(reversed(rings[1])))
    count = len(profile)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((rings[0][i], rings[0][j], rings[1][j], rings[1][i]))


def switch_pocket_cutter(placed, thickness):
    """One cutter mesh containing every switch pocket."""

    bm = bmesh.new()
    for x, y, rotation in placed:
        add_switch_pocket(bm, x, y, rotation, thickness)
    return mesh.mesh_object("switch_pockets", bm)


def socket_pocket_cutters(placed):
    """Body pocket plus one tab extension on the higher pin's side."""

    body_bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(_SOCKET_MID_OFFSET, rotation)
        mesh.add_box(
            body_bm,
            (x + ox, y + oy, (SOCKET_POCKET_DEPTH - EPS) / 2.0),
            (SOCKET_POCKET_LENGTH, SOCKET_POCKET_WIDTH, SOCKET_POCKET_DEPTH + EPS),
            rotation,
        )
    body = mesh.mesh_object("socket_pockets", body_bm)

    half_length = SOCKET_POCKET_LENGTH / 2.0
    tab_width = SOCKET_CONTACT_SIZE + SOCKET_CONTACT_EMBED
    tab_centre = half_length + (SOCKET_CONTACT_SIZE - SOCKET_CONTACT_EMBED) / 2.0
    tab_x = _SOCKET_MID_OFFSET[0] + tab_centre
    tab_y = MX_PIN_POSITIONS[0][1]

    tab_bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate((tab_x, tab_y), rotation)
        mesh.add_box(
            tab_bm,
            (x + ox, y + oy, (SOCKET_POCKET_DEPTH - EPS) / 2.0),
            (tab_width, SOCKET_CONTACT_SIZE, SOCKET_POCKET_DEPTH + EPS),
            rotation,
        )
    tab = mesh.mesh_object("socket_tab", tab_bm)

    return [body, tab]


def led_pocket_cutter(placed):
    """One cutter mesh containing every LED package pocket."""

    bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(LED_OFFSET, rotation)
        mesh.add_box(
            bm,
            (x + ox, y + oy, (LED_POCKET_DEPTH - EPS) / 2.0),
            (LED_POCKET, LED_POCKET, LED_POCKET_DEPTH + EPS),
            rotation,
        )
    return mesh.mesh_object("led_pockets", bm)


def led_light_hole_cutter(placed, thickness):
    """One cutter mesh with a light hole through the plate for every LED."""

    bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(LED_OFFSET, rotation)
        mesh.add_cylinder(
            bm,
            (x + ox, y + oy),
            LED_LIGHT_HOLE_DIAMETER / 2.0,
            -EPS,
            thickness + EPS,
        )
    return mesh.mesh_object("led_light_holes", bm)


def hole_cutter(placed, thickness):
    """Centre post, switch pins and 5-pin plastic legs, through the floor."""

    z_min = -EPS
    z_max = thickness + EPS
    bm = bmesh.new()
    for x, y, rotation in placed:
        mesh.add_cylinder(
            bm, (x, y), MX_CENTER_POST_DIAMETER / 2.0, z_min, z_max
        )
        for px, py in MX_PIN_POSITIONS:
            ox, oy = _rotate((px, py), rotation)
            mesh.add_cylinder(
                bm, (x + ox, y + oy), MX_PIN_HOLE_DIAMETER / 2.0, z_min, z_max
            )
        for px, py in MX_LEG_POSITIONS:
            ox, oy = _rotate((px, py), rotation)
            mesh.add_cylinder(
                bm, (x + ox, y + oy), MX_LEG_HOLE_DIAMETER / 2.0, z_min, z_max
            )
    return mesh.mesh_object("holes", bm)
