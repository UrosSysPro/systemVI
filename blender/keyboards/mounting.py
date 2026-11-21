"""Plate fastening tabs, cover magnets and wire-routing geometry.

The case is split into a bottom tray and an outer edge ring.  The top plate
has lower side tabs that are clamped between the two: the tab sits on the
bottom tray and under the edge ring's internal shelf, and an M2 screw goes up
from a counterbore in the bottom tray through the tab into a vertical heatset
insert in the edge ring.  Tabs are planned adaptively from the plate size
(:func:`plate_tab_plan`): west and east sides get an evenly spaced row, and the
south edge gets a row as well.  The north edge stays free for the bay and cover
lip.  The earlier idea of M3 inserts in the inter-key webs is still rejected:
the 5.05 mm web is too narrow for an insert once walls and the socket tab are
accounted for.

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

# --------------------------------------------------------------------------
# Split case and plate fastening
# --------------------------------------------------------------------------
#
# The case is split into a bottom tray and an outer edge ring.  The top plate
# has lower side tabs that sit on the bottom tray; the edge ring has an
# internal lower recess and an overhanging shelf above the tabs.  M2 screws go
# up from counterbores in the bottom tray, through the tabs, into vertical
# heatset inserts in the edge ring.
#
# The tab is 4 mm high (the lower part of the 9.5 mm plate), which leaves room
# for a 4.5 mm insert under the 15 mm case rim.  The screw sits 2.2 mm
# inboard of the plate edge; the edge ring keeps at least 0.4 mm of material
# around the insert.

TAB_HEIGHT = 4.0           # plate tab height, from the plate bottom
TAB_DEPTH = 3.6            # radial length of a plate tab beyond the outline
TAB_WIDTH = 12.0           # length of a tab along the plate edge
TAB_TARGET_SPACING = 50.0  # target spacing for the adaptive tab rows
TAB_SIDE_MARGIN = 10.0     # preferred gap from a plate corner
TAB_MIN_GAP = 6.0          # minimum gap between two tabs on one side
TAB_OVERLAP = 0.5          # tab runs into the plate for a clean union

SCREW_HOLE_DIAMETER = 2.4       # M2 clearance
SCREW_HEAD_DIAMETER = 4.0       # 3.8 mm M2 head plus print clearance
SCREW_COUNTERBORE_DEPTH = 2.0
SCREW_FROM_EDGE = 2.2           # screw centre inboard of the plate outline
INSERT_HOLE_DIAMETER = 3.0      # M2 heatset insert, nominal 3.0 mm OD
INSERT_HOLE_DEPTH = 4.5


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


@dataclass(frozen=True)
class PlateTab:
    """One lower fastening tab on the top plate.

    ``centre`` is the centre of the tab box in the centred plate frame and
    ``screw`` is the M2 clearance-hole centre, slightly outboard of ``centre``
    because the tab runs into the plate for the boolean union.
    """

    side: str
    centre: tuple[float, float]
    screw: tuple[float, float]
    width: float
    depth: float


@dataclass(frozen=True)
class TabPlan:
    tabs: tuple[PlateTab, ...]

    @property
    def count(self):
        return len(self.tabs)

    def by_side(self, side):
        return tuple(tab for tab in self.tabs if tab.side == side)


def _tab_count(length, minimum=1):
    """Adaptive number of tabs that fit evenly on one plate side."""

    usable = length - 2.0 * TAB_SIDE_MARGIN
    if usable <= 0.0:
        usable = length - 2.0 * TAB_WIDTH
    if usable < TAB_WIDTH:
        raise ValueError(
            f"plate side is too short for a {TAB_WIDTH} mm fastening tab "
            f"({length:.1f} mm available)"
        )

    count = max(1, int(round(usable / TAB_TARGET_SPACING)))
    while count > minimum and (
        count * TAB_WIDTH + (count - 1) * TAB_MIN_GAP > usable
    ):
        count -= 1
    if count * TAB_WIDTH + (count - 1) * TAB_MIN_GAP > usable:
        raise ValueError(
            f"{count} fastening tabs do not fit on a {length:.1f} mm plate "
            f"side; reduce TAB_WIDTH or use a larger plate"
        )
    return count


def plate_tab_plan(width, height):
    """Plan lower side tabs for a centred plate of ``width`` x ``height``.

    West and east sides get an adaptive row along Y; the south edge gets an
    adaptive row along X.  The north edge is left free for the electronics bay
    and cover lip.  Tab centres are evenly distributed along each side, so
    they always stay clear of the corners.
    """

    if not math.isfinite(width) or not math.isfinite(height):
        raise ValueError("plate width and height must be finite")
    if width <= 2.0 * TAB_DEPTH or height <= TAB_DEPTH:
        raise ValueError(
            f"plate is too small for fastening tabs: {width:.1f} x "
            f"{height:.1f} mm"
        )

    half_w = width / 2.0
    half_d = height / 2.0
    tabs = []

    side_count = _tab_count(height)
    for index in range(side_count):
        y = -half_d + height * (index + 1) / (side_count + 1)
        for side in ("west", "east"):
            sign = -1.0 if side == "west" else 1.0
            centre_x = sign * (half_w + TAB_DEPTH / 2.0)
            screw_x = sign * (half_w + SCREW_FROM_EDGE)
            tabs.append(
                PlateTab(
                    side=side,
                    centre=(centre_x, y),
                    screw=(screw_x, y),
                    width=TAB_WIDTH,
                    depth=TAB_DEPTH,
                )
            )

    south_count = _tab_count(width)
    for index in range(south_count):
        x = -half_w + width * (index + 1) / (south_count + 1)
        tabs.append(
            PlateTab(
                side="south",
                centre=(x, -(half_d + TAB_DEPTH / 2.0)),
                screw=(x, -(half_d + SCREW_FROM_EDGE)),
                width=TAB_WIDTH,
                depth=TAB_DEPTH,
            )
        )

    return TabPlan(tuple(tabs))


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
