"""Pure keyboard layout model.

This module deliberately imports neither ``bpy`` nor ``bmesh`` so layouts and
dimensions can be reasoned about (and tested) without Blender.

Coordinates are millimetres, ``+Y`` is north (the function/top row side) and
one keyboard unit is ``PITCH``.  A layout is just a list of :class:`Key`
objects; how that list is turned into a plate, a split half or a curved key
well is the job of the builders.
"""

from __future__ import annotations

import math
from dataclasses import dataclass

PITCH = 19.05                 # 1u
PLATE_MARGIN = 2.0            # default value for each of the four margins

LED_FACING_NORTH = "north"    # switch in its default orientation
LED_FACING_SOUTH = "south"    # whole switch footprint rotated 180 degrees


@dataclass(frozen=True)
class Rect:
    """An axis-aligned rectangle with ``y0`` south and ``y1`` north."""

    x0: float
    x1: float
    y0: float
    y1: float

    @property
    def width(self):
        return self.x1 - self.x0

    @property
    def height(self):
        return self.y1 - self.y0

    @property
    def centre_x(self):
        return (self.x0 + self.x1) / 2.0

    @property
    def centre_y(self):
        return (self.y0 + self.y1) / 2.0


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
            value = getattr(self, name)
            if not math.isfinite(value) or value < 0.0:
                raise ValueError(f"{name} must be a non-negative number")

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
    ``rotation`` is radians around the switch centre.
    """

    x: float
    y: float
    width: float = PITCH
    height: float = PITCH
    rotation: float = 0.0

    def bounds(self):
        """Axis-aligned bounds of this key's rotated cap footprint."""

        half_w = self.width / 2.0
        half_h = self.height / 2.0
        if abs(self.rotation) < 1e-12:
            return Rect(
                self.x - half_w,
                self.x + half_w,
                self.y - half_h,
                self.y + half_h,
            )

        cos_r = math.cos(self.rotation)
        sin_r = math.sin(self.rotation)
        corners = (
            (-half_w, -half_h),
            (half_w, -half_h),
            (half_w, half_h),
            (-half_w, half_h),
        )
        points = [
            (
                self.x + dx * cos_r - dy * sin_r,
                self.y + dx * sin_r + dy * cos_r,
            )
            for dx, dy in corners
        ]
        return Rect(
            min(point[0] for point in points),
            max(point[0] for point in points),
            min(point[1] for point in points),
            max(point[1] for point in points),
        )


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
    """Bounding :class:`Rect` of all key cells, including the four margins.

    ``params`` carries the four independent margins: left/right along X and
    top/bottom along Y (top is +Y, the function row side).
    """

    if not keys:
        raise ValueError("layout has no keys")
    params = params or PlateParams()

    bounds = [key.bounds() for key in keys]
    return Rect(
        min(bound.x0 for bound in bounds) - params.margin_left,
        max(bound.x1 for bound in bounds) + params.margin_right,
        min(bound.y0 for bound in bounds) - params.margin_bottom,
        max(bound.y1 for bound in bounds) + params.margin_top,
    )
