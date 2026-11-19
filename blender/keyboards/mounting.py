"""Cover magnets and wire-routing geometry.

The top plate is **not fastened** at the moment.  The earlier bottom-screw
design needed notches in the case wall for the plate ears, which is not
acceptable.  Moving the heatset inserts between the keys is not possible with
M3 inserts: adjacent 1u switch pockets leave a 5.05 mm web, an M3 insert is
4.6 mm across before any wall thickness, and the hotswap socket tab already
protrudes 0.365 mm into that web.  The plate therefore sits in its pocket,
supported by small ribs on the case floor, until a wall-free fastening is
chosen.

The cover is held by rectangular magnets in two rails inside the electronics
bay.  Only the magnet locations are thickened: the cover keeps its 2.5 mm lid
and gets small 1.5 mm bosses on the underside around each magnet.  The case
rail tops sit 1.5 mm below the bay shelf, so the bosses and magnets meet there
while the rest of the lid rests on the shelf.

The plate pocket floor is recessed by ``wire_gap`` and gets short ribs, one in
each gap between neighbouring keycaps, at the original floor height.
Wires run in the channels between them.

All dimensions are millimetres.  This module is pure Python; it must not
import ``bpy``.
"""

from __future__ import annotations

import math
from dataclasses import dataclass

import layout

# Case floor and lid thickness; the case builder uses the same values.
CASE_FLOOR = 3.0
LID_THICKNESS = 2.5
MAGNET_TOP_SKIN = 0.9

# Magnets.
MAGNET_CLEARANCE = 0.2     # per side
MAGNET_WALL = 1.5
MAGNET_GAP = 1.2           # between two magnets along a rail
MAGNET_END = 0.4           # rail end margin
RAIL_FRONT_GAP = 0.2       # rail starts this far behind the cover lip

# Local cover bosses around the magnets.
BOSS_X_MARGIN = 1.0
BOSS_Y_MARGIN = 0.5
BOSS_LID_OVERLAP = 0.5     # boss runs into the lid for a clean union

# Wire routing: one short rib in each keycap gap.
RIB_WIDTH = 3.0
RIB_MAX_LENGTH = 10.0
RIB_CLEARANCE = 0.5        # between neighbouring ribs
RIB_MIN_LENGTH = 2.0
RIB_MAX_CELL_GAP = 1.5     # only cells that touch (or nearly touch)
RIB_MIN_OVERLAP = 4.0


@dataclass(frozen=True)
class MountingOptions:
    """User-selectable magnet and wire-routing dimensions."""

    magnet_width: float = 6.0
    magnet_length: float = 6.0
    magnet_thickness: float = 3.0
    wire_gap: float = 1.5

    @property
    def magnet_pocket_depth(self):
        return self.magnet_thickness + 0.1

    def magnet_boss_height(self, lid_thickness=LID_THICKNESS):
        """Boss depth below the lid needed to bury the magnet with a top skin."""

        return max(
            0.0,
            self.magnet_pocket_depth - (lid_thickness - MAGNET_TOP_SKIN),
        )

    def validate(self, floor=CASE_FLOOR):
        values = {
            "magnet_width": self.magnet_width,
            "magnet_length": self.magnet_length,
            "magnet_thickness": self.magnet_thickness,
            "wire_gap": self.wire_gap,
        }
        for name, value in values.items():
            if not math.isfinite(value) or value <= 0.0:
                raise ValueError(f"{name} must be a positive number, got {value}")
        if self.wire_gap >= floor - 0.8:
            raise ValueError(
                f"wire_gap must leave at least 0.8 mm of case floor, "
                f"got {self.wire_gap} mm with a {floor} mm floor"
            )
        return self


@dataclass(frozen=True)
class MagnetMount:
    side: str
    centre: tuple[float, float]
    size: tuple[float, float]
    rail: tuple[float, float, float, float]
    rail_top: float
    pocket_depth: float


@dataclass(frozen=True)
class MagnetPlan:
    mounts: tuple[MagnetMount, ...]
    rail_width: float
    pocket_width: float
    pocket_length: float
    pocket_depth: float
    rail_y0: float
    boss_height: float


def magnet_plan(opening, shelf, options, lip_thickness, boss_height):
    """Place two side magnet rails in the bay and their magnet pockets."""

    options.validate()
    pocket_width = options.magnet_width + 2.0 * MAGNET_CLEARANCE
    pocket_length = options.magnet_length + 2.0 * MAGNET_CLEARANCE
    rail_width = pocket_width + 2.0 * MAGNET_WALL
    rail_y0 = opening.y0 + lip_thickness + RAIL_FRONT_GAP
    rail_length = opening.y1 - rail_y0
    pocket_depth = options.magnet_pocket_depth
    rail_top = shelf - boss_height

    if rail_top <= CASE_FLOOR:
        raise ValueError(
            "the cover magnets would sit below the case floor; reduce "
            "magnet_thickness or increase compartment_height"
        )
    if 2.0 * rail_width >= opening.width:
        raise ValueError(
            "electronics bay is too narrow for the side magnet rails; "
            f"bay is {opening.width:.1f} mm, rails need "
            f"{2.0 * rail_width:.1f} mm plus clearance"
        )
    if rail_length < pocket_length + 2.0 * MAGNET_END:
        raise ValueError(
            "electronics bay is too shallow for the magnets; "
            f"usable rail length is {rail_length:.1f} mm, need "
            f"{pocket_length + 2.0 * MAGNET_END:.1f} mm"
        )

    max_count = int(
        math.floor(
            (rail_length - 2.0 * MAGNET_END + MAGNET_GAP)
            / (pocket_length + MAGNET_GAP)
            + 1e-9
        )
    )
    count = max(1, max_count)
    if count * pocket_length + (count - 1) * MAGNET_GAP > rail_length:
        count -= 1
    span = count * pocket_length + (count - 1) * MAGNET_GAP
    start = rail_y0 + (rail_length - span) / 2.0
    centres_y = [
        start + pocket_length / 2.0 + index * (pocket_length + MAGNET_GAP)
        for index in range(count)
    ]

    mounts = []
    for side in ("west", "east"):
        if side == "west":
            rail = (opening.x0, opening.x0 + rail_width, rail_y0, opening.y1)
        else:
            rail = (opening.x1 - rail_width, opening.x1, rail_y0, opening.y1)
        centre_x = (rail[0] + rail[1]) / 2.0
        for centre_y in centres_y:
            mounts.append(
                MagnetMount(
                    side=side,
                    centre=(centre_x, centre_y),
                    size=(pocket_width, pocket_length),
                    rail=rail,
                    rail_top=rail_top,
                    pocket_depth=pocket_depth,
                )
            )

    return MagnetPlan(
        mounts=tuple(mounts),
        rail_width=rail_width,
        pocket_width=pocket_width,
        pocket_length=pocket_length,
        pocket_depth=pocket_depth,
        rail_y0=rail_y0,
        boss_height=boss_height,
    )


def rib_plan(keys, bounds):
    """One short support rib in each gap between neighbouring keycaps.

    Keys are paired when their cap cells touch in X and overlap in Y.  The rib
    is centred on the cap boundary, which is also under solid plate: even for
    a 1u key the nearest switch pocket is 2.525 mm away, more than the rib's
    1.5 mm half-width plus clearance.  Wide keys therefore get ribs at their
    cap edges too.  Overlapping candidates (a 2u-tall key next to two 1u keys)
    are merged by keeping the first.
    """

    ribs = []
    for index, first in enumerate(keys):
        for second in keys[index + 1 :]:
            a = first.bounds()
            b = second.bounds()

            if b.x0 >= a.x1:
                gap = b.x0 - a.x1
                boundary = (a.x1 + b.x0) / 2.0
            elif a.x0 >= b.x1:
                gap = a.x0 - b.x1
                boundary = (a.x0 + b.x1) / 2.0
            else:
                continue
            if gap > RIB_MAX_CELL_GAP:
                continue

            overlap = min(a.y1, b.y1) - max(a.y0, b.y0)
            if overlap < RIB_MIN_OVERLAP:
                continue

            length = min(RIB_MAX_LENGTH, overlap - 2.0 * RIB_CLEARANCE)
            if length < RIB_MIN_LENGTH:
                continue

            centre_x = boundary - bounds.centre_x
            centre_y = (
                (min(a.y1, b.y1) + max(a.y0, b.y0)) / 2.0
                - bounds.centre_y
            )
            rect = layout.Rect(
                centre_x - RIB_WIDTH / 2.0,
                centre_x + RIB_WIDTH / 2.0,
                centre_y - length / 2.0,
                centre_y + length / 2.0,
            )

            if any(
                rect.x0 < other.x1 + RIB_CLEARANCE
                and rect.x1 > other.x0 - RIB_CLEARANCE
                and rect.y0 < other.y1 + RIB_CLEARANCE
                and rect.y1 > other.y0 - RIB_CLEARANCE
                for other in ribs
            ):
                continue
            ribs.append(rect)

    return tuple(ribs)
