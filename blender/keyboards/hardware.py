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

import layout
import mesh

# --------------------------------------------------------------------------
# MX switch (5 pin), seen from the switch centre; +y points "north"
# --------------------------------------------------------------------------

SWITCH_TOLERANCE = 0.05          # per side clearance around the switch housing
MX_POCKET = 14.0 + 2.0 * SWITCH_TOLERANCE   # 14.1 mm band at the plate surface
MX_POCKET_CLIP_X = 14.0 + 2.0 * SWITCH_TOLERANCE  # 14.1 mm, X direction
MX_POCKET_CLIP_Y = 15.0 + 2.0 * SWITCH_TOLERANCE  # 15.1 mm, Y direction
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

SOCKET_POCKET_LENGTH = 14.0      # X, body envelope (12 mm base + 2 mm extension)
SOCKET_POCKET_WIDTH = 9.0        # Y, known-good body envelope
SOCKET_POCKET_EXTENSION = 2.0    # extra X on the side nearest the switch centre
SOCKET_CONTACT_SIZE = 3.0        # protruding metal contact tab, 3 x 3 mm
SOCKET_CONTACT_EMBED = 0.2       # overlap into the body pocket
SOCKET_POCKET_DEPTH = 3.0

# The contact tabs stay where the known-good 12 mm body put them: the extra
# 2 mm grows only the +X body face, so the tab anchors use the base length.
SOCKET_POCKET_BASE_LENGTH = SOCKET_POCKET_LENGTH - SOCKET_POCKET_EXTENSION

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

# The hotswap body pocket is a 14 x 9 mm envelope: the proven 12 x 9 mm body
# plus 2 mm extra X on the side nearest the switch centre (the +X pin side).
# Each metal contact tab is a separate 3 x 3 mm extension beyond the original
# 12 mm body edge, on that pin's X side and at that pin's Y.  The two tabs are
# therefore diagonal, not mirror-symmetric: the higher pin exits +X, the lower
# one -X.
_SOCKET_MID_OFFSET = (-0.635, -3.81)   # base body centre; the tab anchor
_SOCKET_BODY_OFFSET = (
    _SOCKET_MID_OFFSET[0] + SOCKET_POCKET_EXTENSION / 2.0,
    _SOCKET_MID_OFFSET[1],
)


def _rotate(offset, angle):
    """Rotate a 2D offset by ``angle`` radians."""

    c = math.cos(angle)
    s = math.sin(angle)
    return (offset[0] * c - offset[1] * s, offset[0] * s + offset[1] * c)


def _clip_relief_profile(clip_half, band_half, z_bottom, z_step, z_top):
    """Closed eight-point profile (relief axis, Z) with a sharp step.

    The lower part opens up to ``clip_half`` so the switch clip can spring
    out; above ``z_step`` the pocket returns to the ``band_half`` surface
    opening, leaving a horizontal shoulder that the clip catches on.
    """

    return (
        (-clip_half, z_bottom),
        (clip_half, z_bottom),
        (clip_half, z_step),
        (band_half, z_step),
        (band_half, z_top),
        (-band_half, z_top),
        (-band_half, z_step),
        (-clip_half, z_step),
    )


def add_switch_pocket(bm, x, y, rotation, thickness):
    """Add one stepped MX pocket to ``bm``.

    The surface band is a 14.1 x 14.1 mm opening (the 14 x 14 mm switch
    housing plus ``SWITCH_TOLERANCE`` on every side).  One millimetre below it
    the pocket widens by the clip relief so the MX clips can spring back out;
    with the current constants that relief is 14.1 x 15.1 mm, i.e. the extra
    clearance is on the north and south edges where the clips sit.

    The stepped profile is built in the plane of the relief axis and
    extruded across the other axis, so the transition stays a flat shoulder
    instead of becoming a taper.  Relief on both axes at once is rejected:
    an MX switch only clips north/south (or east/west), never both.
    """

    band = MX_POCKET / 2.0
    clip_x = MX_POCKET_CLIP_X / 2.0
    clip_y = MX_POCKET_CLIP_Y / 2.0

    if clip_x < band or clip_y < band:
        raise ValueError(
            "switch pocket clip relief must not be smaller than the "
            f"{MX_POCKET} mm surface band, got "
            f"{MX_POCKET_CLIP_X} x {MX_POCKET_CLIP_Y} mm"
        )

    relief_x = clip_x > band
    relief_y = clip_y > band
    if relief_x and relief_y:
        raise ValueError(
            "switch pocket clip relief must be on one axis only, got "
            f"{MX_POCKET_CLIP_X} x {MX_POCKET_CLIP_Y} mm under a "
            f"{MX_POCKET} mm surface band"
        )

    z_bottom = thickness - MX_POCKET_DEPTH
    z_step = thickness - CLIP_RELIEF_DEPTH
    z_top = thickness + EPS

    if not (relief_x or relief_y):
        mesh.add_box(
            bm,
            (x, y, (z_bottom + z_top) / 2.0),
            (MX_POCKET, MX_POCKET, z_top - z_bottom),
            rotation,
        )
        return

    if relief_y:
        profile = _clip_relief_profile(
            clip_y, band, z_bottom, z_step, z_top
        )
    else:
        profile = _clip_relief_profile(
            clip_x, band, z_bottom, z_step, z_top
        )

    cos_r = math.cos(rotation)
    sin_r = math.sin(rotation)
    rings = []
    for side in (-1.0, 1.0):
        ring = []
        for offset, z in profile:
            if relief_y:
                local_x, local_y = side * band, offset
            else:
                local_x, local_y = offset, side * band
            wx = x + local_x * cos_r - local_y * sin_r
            wy = y + local_x * sin_r + local_y * cos_r
            ring.append(bm.verts.new((wx, wy, z)))
        rings.append(ring)

    bm.faces.new(tuple(rings[0]))
    bm.faces.new(tuple(reversed(rings[1])))
    count = len(profile)
    for i in range(count):
        j = (i + 1) % count
        bm.faces.new((rings[0][i], rings[0][j], rings[1][j], rings[1][i]))


def switch_pocket_cutter(placed, thickness):
    """One cutter mesh containing every switch pocket.

    The cutter is validated too: a malformed stepped profile (for example a
    zero-width clip step) used to slip through because only the final plate
    was checked.  Fail here instead of exporting a pocket without relief.
    """

    bm = bmesh.new()
    for x, y, rotation in placed:
        add_switch_pocket(bm, x, y, rotation, thickness)
    pocket = mesh.mesh_object("switch_pockets", bm)
    mesh.check_mesh("switch_pockets", pocket, strict=True)
    return pocket


def socket_contact_offsets():
    """Plan offsets of the two metal-contact tab clearances from the switch.

    A hotswap socket has two metal contacts, one per switch pin.  Each one
    protrudes past the body edge on its own pin's side, at that pin's Y, so
    the pair is diagonal rather than symmetric about the body.
    """

    half_length = SOCKET_POCKET_BASE_LENGTH / 2.0
    tab_centre = (
        half_length + (SOCKET_CONTACT_SIZE - SOCKET_CONTACT_EMBED) / 2.0
    )
    offsets = []
    for pin_x, pin_y in MX_PIN_POSITIONS:
        side = 1.0 if pin_x > _SOCKET_MID_OFFSET[0] else -1.0
        offsets.append((_SOCKET_MID_OFFSET[0] + side * tab_centre, pin_y))
    return tuple(offsets)


def socket_pocket_cutters(placed):
    """Body pocket plus one tab clearance on each metal contact's side."""

    body_bm = bmesh.new()
    for x, y, rotation in placed:
        ox, oy = _rotate(_SOCKET_BODY_OFFSET, rotation)
        mesh.add_box(
            body_bm,
            (x + ox, y + oy, (SOCKET_POCKET_DEPTH - EPS) / 2.0),
            (SOCKET_POCKET_LENGTH, SOCKET_POCKET_WIDTH, SOCKET_POCKET_DEPTH + EPS),
            rotation,
        )
    body = mesh.mesh_object("socket_pockets", body_bm)

    tab_width = SOCKET_CONTACT_SIZE + SOCKET_CONTACT_EMBED
    contact_offsets = socket_contact_offsets()

    tab_bm = bmesh.new()
    for x, y, rotation in placed:
        for contact_x, contact_y in contact_offsets:
            ox, oy = _rotate((contact_x, contact_y), rotation)
            mesh.add_box(
                tab_bm,
                (x + ox, y + oy, (SOCKET_POCKET_DEPTH - EPS) / 2.0),
                (tab_width, SOCKET_CONTACT_SIZE, SOCKET_POCKET_DEPTH + EPS),
                rotation,
            )
    tab = mesh.mesh_object("socket_tabs", tab_bm)

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


# --------------------------------------------------------------------------
# Plate mounting screws
# --------------------------------------------------------------------------
#
# Two thin wood screws enter from the bottom tray and thread into blind pilot
# holes in the top plate.  Each screw sits in a web where three keycaps meet,
# so it is clear of every switch, socket and LED cutout.  The position plan is
# pure 2D geometry shared by the plate and case builders.

SCREW_HOLE_DIAMETER = 3.0          # thin wood screw, nominal 3 mm
SCREW_COUNTERSINK_DIAMETER = 6.0   # 90-degree countersunk head
SCREW_COUNTERSINK_DEPTH = 1.5
PLATE_SCREW_PILOT_DEPTH = 7.0      # blind, leaves a 2.5 mm top skin

# Cutout envelopes used only to keep the screw holes out of the plate features.
_SCREW_SWITCH_HALF = (MX_POCKET / 2.0, MX_POCKET_CLIP_Y / 2.0)
_SCREW_SOCKET_HALF = (SOCKET_POCKET_LENGTH / 2.0, SOCKET_POCKET_WIDTH / 2.0)
_SCREW_LED_HALF = (LED_POCKET / 2.0, LED_POCKET / 2.0)
_SCREW_TAB_OFFSETS = socket_contact_offsets()
_SCREW_FEATURE_RECTS = (
    ((0.0, 0.0), _SCREW_SWITCH_HALF),
    (_SOCKET_BODY_OFFSET, _SCREW_SOCKET_HALF),
    (LED_OFFSET, _SCREW_LED_HALF),
) + tuple(
    (offset, (SOCKET_CONTACT_SIZE / 2.0, SOCKET_CONTACT_SIZE / 2.0))
    for offset in _SCREW_TAB_OFFSETS
)
_SCREW_FEATURE_CIRCLES = (
    ((0.0, 0.0), MX_CENTER_POST_DIAMETER / 2.0),
    (MX_PIN_POSITIONS[0], MX_PIN_HOLE_DIAMETER / 2.0),
    (MX_PIN_POSITIONS[1], MX_PIN_HOLE_DIAMETER / 2.0),
    (MX_LEG_POSITIONS[0], MX_LEG_HOLE_DIAMETER / 2.0),
    (MX_LEG_POSITIONS[1], MX_LEG_HOLE_DIAMETER / 2.0),
    (LED_OFFSET, LED_LIGHT_HOLE_DIAMETER / 2.0),
)

_PLATE_SCREW_CACHE = {}


def _rotate_point(point, angle):
    c = math.cos(angle)
    s = math.sin(angle)
    return (point[0] * c - point[1] * s, point[0] * s + point[1] * c)


def _rect_distance(point, centre, half):
    dx = abs(point[0] - centre[0]) - half[0]
    dy = abs(point[1] - centre[1]) - half[1]
    if dx <= 0.0 and dy <= 0.0:
        return -min(-dx, -dy)
    return math.hypot(max(dx, 0.0), max(dy, 0.0))


def _segment_distance(point, first, second):
    dx = second[0] - first[0]
    dy = second[1] - first[1]
    length_sq = dx * dx + dy * dy
    if length_sq <= 1e-12:
        return math.hypot(point[0] - first[0], point[1] - first[1])
    t = (
        (point[0] - first[0]) * dx + (point[1] - first[1]) * dy
    ) / length_sq
    t = max(0.0, min(1.0, t))
    return math.hypot(
        point[0] - (first[0] + t * dx),
        point[1] - (first[1] + t * dy),
    )


def _point_in_polygon(point, polygon):
    inside = False
    count = len(polygon)
    for index in range(count):
        x0, y0 = polygon[index]
        x1, y1 = polygon[(index + 1) % count]
        crosses = (y0 > point[1]) != (y1 > point[1])
        if crosses:
            x = x0 + (point[1] - y0) * (x1 - x0) / (y1 - y0)
            if point[0] < x:
                inside = not inside
    return inside


def _polygon_distance(point, polygon):
    best = min(
        _segment_distance(point, polygon[index], polygon[(index + 1) % len(polygon)])
        for index in range(len(polygon))
    )
    if _point_in_polygon(point, polygon):
        return -best
    return best


def _stabilizer_obstacles(placed):
    """Plate polygons of the stabilizer post holes and wire channels."""

    import stabilizer

    obstacles = []
    for x, y, rotation, _rect, width, height in placed:
        orientation = stabilizer.stabilizer_orientation(width, height)
        if orientation is None:
            continue
        dimension = (
            width
            if orientation == stabilizer.STAB_HORIZONTAL
            else height
        )
        offset = stabilizer.wire_offset(dimension)
        local_rotation = rotation
        if orientation == stabilizer.STAB_VERTICAL:
            local_rotation -= math.pi / 2.0

        for sign in (-1.0, 1.0):
            dx, dy = _rotate_point((sign * offset, 0.0), local_rotation)
            cx = x + dx
            cy = y + dy
            half = stabilizer.POST_HOLE / 2.0
            corners = [
                _rotate_point((sx * half, sy * half), local_rotation)
                for sx, sy in ((-1, -1), (1, -1), (1, 1), (-1, 1))
            ]
            obstacles.append([(cx + px, cy + py) for px, py in corners])

        obstacles.append(
            stabilizer._transform(
                stabilizer._channel_profile(offset),
                x,
                y,
                local_rotation,
            )
        )
    return obstacles


def _screw_clearance(point, placed, obstacles=()):
    """Distance from ``point`` to the nearest plate cutout envelope."""

    best = 1e9
    for x, y, rotation, _rect, _width, _height in placed:
        local = _rotate_point(
            (point[0] - x, point[1] - y), -rotation
        )
        for centre, half in _SCREW_FEATURE_RECTS:
            best = min(best, _rect_distance(local, centre, half))
        for centre, radius in _SCREW_FEATURE_CIRCLES:
            best = min(
                best,
                math.hypot(local[0] - centre[0], local[1] - centre[1])
                - radius,
            )
    for polygon in obstacles:
        best = min(best, _polygon_distance(point, polygon))
    return best


def _triple_junctions(placed):
    """Points in the plate frame where three or more keycap cells meet."""

    xs = set()
    ys = set()
    for _x, _y, _rotation, rect, _width, _height in placed:
        xs.update((round(rect.x0, 6), round(rect.x1, 6)))
        ys.update((round(rect.y0, 6), round(rect.y1, 6)))

    points = {}
    for x in xs:
        for y in ys:
            touching = sum(
                1
                for _kx, _ky, _rot, rect, _width, _height in placed
                if rect.x0 - 1e-6 <= x <= rect.x1 + 1e-6
                and rect.y0 - 1e-6 <= y <= rect.y1 + 1e-6
            )
            if touching >= 3:
                points[(x, y)] = touching
    return points


def _optimized_screw_point(point, placed, obstacles, radius=2.0, step=0.5):
    """Nudge a junction within ``radius`` to maximise cutout clearance."""

    best = (_screw_clearance(point, placed, obstacles), point)
    count = int(round(radius / step))
    for ix in range(-count, count + 1):
        for iy in range(-count, count + 1):
            x = point[0] + ix * step
            y = point[1] + iy * step
            if math.hypot(x - point[0], y - point[1]) > radius + 1e-9:
                continue
            clearance = _screw_clearance((x, y), placed, obstacles)
            if clearance > best[0]:
                best = (clearance, (x, y))
    return best[0], best[1][0], best[1][1]


def plate_screw_plan(keys, params=None):
    """Choose the two middle wood-screw pilot holes in the top plate.

    Each hole lies in the web where three keycaps meet, roughly half way
    between the plate centre and its left/right sides.  The plan picks the
    most central pair with enough separation, then nudges each hole by up to
    2 mm to keep a solid wall around it.
    """

    params = params or layout.PlateParams()
    cache_key = (
        params.margin_left,
        params.margin_right,
        params.margin_top,
        params.margin_bottom,
        params.led_facing,
        tuple(
            (
                round(key.x, 6),
                round(key.y, 6),
                round(key.width, 6),
                round(key.height, 6),
                round(key.rotation, 9),
            )
            for key in keys
        ),
    )
    cached = _PLATE_SCREW_CACHE.get(cache_key)
    if cached is not None:
        return cached

    bounds = layout.layout_bounds(keys, params)
    facing = params.facing_angle()
    placed = []
    for key in keys:
        raw = key.bounds()
        rect = layout.Rect(
            raw.x0 - bounds.centre_x,
            raw.x1 - bounds.centre_x,
            raw.y0 - bounds.centre_y,
            raw.y1 - bounds.centre_y,
        )
        placed.append(
            (
                key.x - bounds.centre_x,
                key.y - bounds.centre_y,
                key.rotation + facing,
                rect,
                key.width,
                key.height,
            )
        )
    obstacles = _stabilizer_obstacles(placed)

    candidates = []
    for point, count in sorted(_triple_junctions(placed).items()):
        clearance, x, y = _optimized_screw_point(
            point, placed, obstacles
        )
        candidates.append((x, y, clearance, count))
    if len(candidates) < 2:
        raise ValueError(
            "the layout does not have two key junctions for the plate "
            "mounting screws"
        )

    # A 3 mm hole needs at least 0.7 mm of material to every cutout.  A
    # junction inside a stabilizer channel or post hole is rejected.
    usable = [item for item in candidates if item[2] >= 2.2]
    if len(usable) < 2:
        usable = candidates

    left = [item for item in usable if item[0] < -0.02 * bounds.width]
    right = [item for item in usable if item[0] > 0.02 * bounds.width]
    best = None
    for a in left:
        for b in right:
            separation = b[0] - a[0]
            if separation < 0.30 * bounds.width:
                continue
            score = (
                0 if a[3] == 3 and b[3] == 3 else 1,
                round(abs(a[1] - b[1]), 1),
                round(abs(a[1]) + abs(b[1]), 1),
                round(abs(a[0] + b[0]), 1),
                -min(a[2], b[2]),
                -separation,
            )
            if best is None or score < best[0]:
                best = (score, a, b)

    if best is None:
        # Short boards can have a very narrow web between the two junctions;
        # keep the most separated pair instead of failing.
        for a in left:
            for b in right:
                separation = b[0] - a[0]
                score = (
                    0 if a[3] == 3 and b[3] == 3 else 1,
                    round(abs(a[1] - b[1]), 1),
                    round(abs(a[1]) + abs(b[1]), 1),
                    round(abs(a[0] + b[0]), 1),
                    -min(a[2], b[2]),
                    -separation,
                )
                if best is None or score < best[0]:
                    best = (score, a, b)
    if best is None:
        raise ValueError(
            "the layout does not have a left/right pair of key junctions for "
            "the plate mounting screws"
        )

    chosen = sorted((best[1][:2], best[2][:2]))
    result = tuple((round(x, 4), round(y, 4)) for x, y in chosen)
    _PLATE_SCREW_CACHE[cache_key] = result
    return result
