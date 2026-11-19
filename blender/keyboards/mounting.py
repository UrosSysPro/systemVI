"""Cover magnets and wire-routing geometry.

The top plate is **not fastened** at the moment.  The earlier bottom-screw
design needed notches in the case wall for the plate ears, which is not
acceptable.  Moving the heatset inserts between the keys is not possible with
M3 inserts: adjacent 1u switch pockets leave a 5.05 mm web, an M3 insert is
4.6 mm across before any wall thickness, and the hotswap socket tab already
protrudes 0.365 mm into that web.  The plate therefore sits in its pocket,
supported by small ribs on the case floor, until a wall-free fastening is
chosen.

The cover is held by rectangular magnets.  Each side edge carries two magnets,
evenly spaced along the side of the compartment.  An adaptive, evenly spaced row
runs along the top (north) edge based on the available width and the requested
spacing.  The top row starts to the right of the USB-C cutout, which occupies
the west part of the north wall.  Every magnet gets its own small case shelf;
there is no continuous rail.

Only the magnet locations are thickened: the cover keeps its 2.5 mm lid and gets
small 1.5 mm bosses on the underside around each magnet.  The small case shelf
tops sit 1.5 mm below the bay shelf, so the bosses and magnets meet there while
the rest of the lid rests on the shelf.

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
MAGNET_GAP = 1.2           # between two magnets along a shelf
MAGNET_SHELF_END = 0.4     # shelf material beyond the pocket ends
RAIL_FRONT_GAP = 0.2       # shelf starts this far behind the cover lip
TOP_EDGE_MARGIN = 1.5      # top-row margin to the side magnet positions
SIDE_MIN_GAP = 0.4         # minimum pocket-to-pocket gap on a side shelf

# Local cover bosses around the magnets.
BOSS_X_MARGIN = 1.0
BOSS_Y_MARGIN = 0.2
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
    magnet_length: float = 3.0
    magnet_thickness: float = 3.0
    magnet_spacing: float = 40.0
    wire_gap: float = 1.5

    def magnet_pocket_depth(self, vertical):
        """Pocket depth for a magnet whose vertical size is ``vertical``."""

        return vertical + 0.1

    def magnet_boss_height(self, vertical, lid_thickness=LID_THICKNESS):
        """Boss depth below the lid needed to bury the magnet with a top skin."""

        return max(
            0.0,
            self.magnet_pocket_depth(vertical)
            - (lid_thickness - MAGNET_TOP_SKIN),
        )

    def validate(self, floor=CASE_FLOOR):
        values = {
            "magnet_width": self.magnet_width,
            "magnet_length": self.magnet_length,
            "magnet_thickness": self.magnet_thickness,
            "magnet_spacing": self.magnet_spacing,
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
    shelf_width: float
    shelf_length: float
    rail_top: float
    pocket_depth: float


@dataclass(frozen=True)
class MagnetPlan:
    mounts: tuple[MagnetMount, ...]
    rail_y0: float
    top_count: int


def magnet_plan(
    opening,
    cavity,
    shelf,
    options,
    lip_thickness,
    north_start,
):
    """Place the side and top magnet positions and all magnet pockets.

    Every magnet sits on its own small shelf inside the bay cavity; the cover
    shelf ring is outside them, so the 45-degree undersides never cross the
    cavity wall.  The longest magnet side always follows the edge: 6 mm along Y
    on the side edges and 6 mm along X on the top edge, with 3 mm vertical.

    Two 6 mm side magnets nearly fill the 13.3 mm side span.  If two fit they
    sit at the front and rear ends with a narrow gap; if the span is too short
    for two, each side gets one centered magnet instead.  The top row starts at
    ``north_start`` (the east edge of the USB-C recess) and is evenly spaced.
    """

    options.validate()

    # Both rows are 3 mm thick.  The 6 mm long side follows the edge; the
    # remaining 3 mm side crosses the edge.
    side_along = options.magnet_width + 2.0 * MAGNET_CLEARANCE
    side_across = options.magnet_length + 2.0 * MAGNET_CLEARANCE
    side_shelf_width = side_across + 2.0 * MAGNET_WALL

    top_along = options.magnet_width + 2.0 * MAGNET_CLEARANCE
    top_across = options.magnet_length + 2.0 * MAGNET_CLEARANCE
    top_shelf_width = top_across + 2.0 * MAGNET_WALL

    pocket_depth = options.magnet_pocket_depth(options.magnet_thickness)
    boss_height = options.magnet_boss_height(options.magnet_thickness)
    rail_top = shelf - boss_height
    rail_y0 = opening.y0 + lip_thickness + RAIL_FRONT_GAP
    rail_length = cavity.y1 - rail_y0

    if rail_top <= CASE_FLOOR:
        raise ValueError(
            "the cover magnets would sit below the case floor; reduce "
            "magnet dimensions or increase compartment_height"
        )
    if 2.0 * side_shelf_width >= cavity.width:
        raise ValueError(
            "electronics bay is too narrow for the side magnet shelves; "
            f"cavity is {cavity.width:.1f} mm, shelves need "
            f"{2.0 * side_shelf_width:.1f} mm plus clearance"
        )

    side_fits_one = side_along <= rail_length
    side_fits_two = 2.0 * side_along + SIDE_MIN_GAP <= rail_length
    if not side_fits_one:
        raise ValueError(
            "the side edge is too short for even one magnet with its long "
            f"side along the edge; {options.magnet_width:.1f} mm magnets need "
            f"more than the {rail_length:.1f} mm side span"
        )

    if side_fits_two:
        side_centres_y = (
            rail_y0 + side_along / 2.0,
            cavity.y1 - side_along / 2.0,
        )
    else:
        side_centres_y = ((rail_y0 + cavity.y1) / 2.0,)

    mounts = []
    for side in ("west", "east"):
        if side == "west":
            rail = (
                cavity.x0,
                cavity.x0 + side_shelf_width,
                rail_y0,
                cavity.y1,
            )
        else:
            rail = (
                cavity.x1 - side_shelf_width,
                cavity.x1,
                rail_y0,
                cavity.y1,
            )
        centre_x = (rail[0] + rail[1]) / 2.0
        for centre_y in side_centres_y:
            mounts.append(
                MagnetMount(
                    side=side,
                    centre=(centre_x, centre_y),
                    size=(side_across, side_along),
                    rail=rail,
                    shelf_width=side_shelf_width,
                    shelf_length=side_along,
                    rail_top=rail_top,
                    pocket_depth=pocket_depth,
                )
            )

    top_count = 0
    top_x0 = north_start + TOP_EDGE_MARGIN
    top_x1 = cavity.x1 - side_shelf_width - TOP_EDGE_MARGIN
    top_width = top_x1 - top_x0
    if top_width >= top_along:
        target = max(
            1, int(math.ceil(top_width / options.magnet_spacing))
        )
        spacing_min = top_along + MAGNET_GAP
        max_fit = int(math.floor(top_width / spacing_min - 1.0 + 1e-9))
        top_count = max(0, min(target, max_fit))
        step = top_width / (top_count + 1.0)
        centres_x = [
            top_x0 + step * (index + 1)
            for index in range(top_count)
        ]
        north_rail = (
            north_start,
            cavity.x1,
            cavity.y1 - top_shelf_width,
            cavity.y1,
        )
        north_y = cavity.y1 - top_shelf_width / 2.0
        for centre_x in centres_x:
            mounts.append(
                MagnetMount(
                    side="north",
                    centre=(centre_x, north_y),
                    size=(top_along, top_across),
                    rail=north_rail,
                    shelf_width=top_shelf_width,
                    shelf_length=top_along + 2.0 * MAGNET_SHELF_END,
                    rail_top=rail_top,
                    pocket_depth=pocket_depth,
                )
            )

    return MagnetPlan(
        mounts=tuple(mounts),
        rail_y0=rail_y0,
        top_count=top_count,
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
