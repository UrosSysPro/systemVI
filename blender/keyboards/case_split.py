"""Split a wide case edge ring into four dovetailed pieces.

The edge ring is a closed rectangular ring.  Two vertical cuts along X at one
quarter and three quarters of the case width produce the same 1:2:1 sections
that the split top plate uses; because the ring is closed, the two cuts turn it
into four printable pieces:

- ``west``: west wall plus the west stubs of the north and south walls,
- ``east``: east wall plus the east stubs,
- ``north``: the middle north rail,
- ``south``: the middle south rail.

Both cuts cross the north and south walls, so each seam gets two dovetails.  The
middle rails carry the male dovetails; the two side pieces get the matching
slots.  A dovetail is a vertical prism that starts 1.5 mm above the plate-tab shelf
(z = 8.5 mm) and stops 2.5 mm below the wall top.  The slot is cut right
through the bottom of the side piece and stops just above the plug top: it is
open at the bottom and closed by the side piece's own material (the cap) at the
top.  The side pieces therefore drop onto the rails from above, with the male
plug entering the slot through its bottom opening; the cap then traps the plug
vertically.  The wide tip is away from the rail end, which locks the pieces
against separation along X.

The dovetail sits inside the wall.  Its Y position leaves material at both wall
faces and it stays clear of the 1.5 mm top chamfer.  The seam gap and the slot
clearance are both 0.2 mm; glue holds the ring closed and the bottom tray and
plate tabs keep the stack aligned.  The male plug is printed smaller than the
slot (0.15 mm extra clearance per side and 0.5 mm shorter) so the pieces slide
together instead of binding.

The dovetail's underside overhangs the lower recess / open slot by about
3.6 mm.  Print the rails with support under the dovetail, or orient them so that
underside is not a downward-facing overhang.

All four pieces are validated strictly before export.
"""

from __future__ import annotations

import math
import os

import bmesh

import case
import mesh

# --------------------------------------------------------------------------
# Split geometry
# --------------------------------------------------------------------------

SPLIT_CASE_MIN_WIDTH = 50.0    # split the edge ring above this case width
SPLIT_CASE_MAX_WIDTH = 250.0   # target maximum printable piece width
SPLIT_RATIO = (1, 2, 1)        # widths of the west, middle and east sections

SEAM_GAP = 0.2                 # total gap between two mating end faces
SIDE_CLEARANCE = 0.15          # per side between male plug and female slot
CAP_CLEARANCE = 0.15           # free gap above the male plug
# Female slot: 0.15 mm larger per side and 0.15 mm deeper than the male plug.
DOVETAIL_NECK = 2.5            # slot width at the rail's end face
DOVETAIL_TAIL = 3.55           # slot width at the far tip
DOVETAIL_DEPTH = 3.65          # slot reach from the seam into the side piece
# Male plug.  It is generated as the female slot offset inward by
# SIDE_CLEARANCE, so every side has the same fit.
PLUG_DEPTH = 3.5               # plug reach from the seam into the side piece
DOVETAIL_MARGIN = 0.4          # wall material kept at both faces
DOVETAIL_START = case.TAB_TOP  # plate-tab shelf, z = 7 mm
DOVETAIL_LIFT = 1.5            # plug bottom above the plate-tab shelf
DOVETAIL_TOP_GAP = 2.5         # wall left above the dovetail / slot at the rim

JOINT_BOTTOM = DOVETAIL_START + DOVETAIL_LIFT

PIECE_SUFFIXES = ("west", "east", "north", "south")

_BIG = 1000.0


def should_split(metrics):
    """Whether a case of these metrics needs a split edge ring."""

    return metrics.width > SPLIT_CASE_MIN_WIDTH


def split_positions(width):
    """X positions of the two cuts for a case ``width`` mm wide."""

    width = float(width)
    if not math.isfinite(width) or width <= 0.0:
        raise ValueError(f"case width must be positive and finite, got {width}")

    quarter = width / sum(SPLIT_RATIO)
    middle = 2.0 * quarter + 2.0 * DOVETAIL_DEPTH
    if middle > SPLIT_CASE_MAX_WIDTH + 1e-9:
        raise ValueError(
            f"the 1:2:1 split of a {width:.1f} mm case still leaves "
            f"{middle:.1f} mm wide middle piece, wider than "
            f"{SPLIT_CASE_MAX_WIDTH:.0f} mm; more cuts or a larger printer "
            "are needed"
        )
    return (-quarter, quarter)


def piece_path(case_edges_path, suffix):
    """Output path for one split ring piece."""

    stem, extension = os.path.splitext(case_edges_path)
    extension = extension or ".stl"
    return f"{stem}_{suffix}{extension}"


# --------------------------------------------------------------------------
# Wall bands and dovetail shapes
# --------------------------------------------------------------------------


def _wall_band(metrics, side):
    """Y range available for the dovetail in one wall, and the joint top.

    The band is measured from the wall's inner face and stops 1.5 mm short of
    the outer face, because the top chamfer removes the outer 1.5 mm at the
    rim.  The returned range is sorted low-to-high in Y.
    """

    if side == "south":
        inner = metrics.plate_pocket.y0
        outer_at_top = metrics.south + case.CHAMFER
        z_top = case.CASE_HEIGHT
        direction = -1.0
    elif side == "north":
        inner = metrics.bay_opening.y1
        outer_at_top = metrics.north - case.CHAMFER
        z_top = metrics.height
        direction = 1.0
    else:
        raise ValueError(f"unknown wall side {side!r}")

    available = (outer_at_top - inner) * direction
    needed = DOVETAIL_TAIL + 2.0 * DOVETAIL_MARGIN
    if available < needed - 1e-9:
        raise ValueError(
            f"the {side} wall is too thin for the dovetail joint: needs "
            f"{needed:.1f} mm of wall at the rim, has {available:.1f} mm"
        )
    if z_top - DOVETAIL_TOP_GAP <= JOINT_BOTTOM + 1.0:
        raise ValueError(
            f"the {side} wall is too low for the set-back dovetail joint: the "
            f"joint needs more than {JOINT_BOTTOM + DOVETAIL_TOP_GAP + 1.0:.1f} mm, "
            f"the wall top is at {z_top:.1f} mm"
        )

    low = inner + direction * DOVETAIL_MARGIN
    high = inner + direction * (available - DOVETAIL_MARGIN)
    return min(low, high), max(low, high), z_top


def _taper_points(x_mouth, x_tip, y_centre, half_mouth, half_tip):
    """Closed trapezoid in the X/Y plane, wider at ``x_tip``."""

    return (
        (x_mouth, y_centre - half_mouth),
        (x_mouth, y_centre + half_mouth),
        (x_tip, y_centre + half_tip),
        (x_tip, y_centre - half_tip),
    )


def _joint_solids(metrics, cut, sign, side):
    """Male plug and matching female slot for one seam and one wall.

    ``sign`` is +1 when the side piece is east of the cut and -1 when it is
    west.  The plug belongs to the middle rail and reaches across the seam into
    the side piece; the slot is cut into the side piece.
    """

    y0, y1, z_top = _wall_band(metrics, side)
    y_centre = (y0 + y1) / 2.0
    middle_face = cut - sign * SEAM_GAP / 2.0
    side_face = cut + sign * SEAM_GAP / 2.0
    tip = cut + sign * DOVETAIL_DEPTH
    plug_top = z_top - DOVETAIL_TOP_GAP

    slot_mouth = side_face - sign * 0.5
    slot_tip = tip

    def slot_half_at(x):
        t = (x - slot_mouth) / (slot_tip - slot_mouth)
        return (
            DOVETAIL_NECK / 2.0
            + t * (DOVETAIL_TAIL - DOVETAIL_NECK) / 2.0
        )

    # The plug is the slot profile offset 0.15 mm inward on each side and
    # 0.15 mm shorter at the tip.  The root starts 0.5 mm inside the rail for
    # a clean boolean union; its top is held DOVETAIL_TOP_GAP below the wall
    # top and its bottom stops 1.5 mm above the plate-tab shelf.
    plug_tip = cut + sign * PLUG_DEPTH
    root = middle_face - sign * 0.5
    plug_points = _taper_points(
        root,
        plug_tip,
        y_centre,
        slot_half_at(root) - SIDE_CLEARANCE,
        slot_half_at(plug_tip) - SIDE_CLEARANCE,
    )
    plug_bottom = JOINT_BOTTOM
    plug_bm = bmesh.new()
    mesh.add_prism_z(plug_bm, plug_points, plug_bottom, plug_top)
    plug = mesh.mesh_object(f"dovetail_plug_{side}", plug_bm)

    # The slot starts 0.5 mm on the open side of the side piece's face so the
    # notch is open.  It is cut right through the bottom of the side piece so
    # the female part can drop onto the male, and stops just above the plug top
    # so the side piece keeps a cap over it.
    slot_points = _taper_points(
        slot_mouth,
        slot_tip,
        y_centre,
        slot_half_at(slot_mouth),
        slot_half_at(slot_tip),
    )
    slot_bm = bmesh.new()
    mesh.add_prism_z(
        slot_bm,
        slot_points,
        -1.0,
        plug_top + CAP_CLEARANCE,
    )
    slot = mesh.mesh_object(f"dovetail_slot_{side}", slot_bm)
    return plug, slot


# --------------------------------------------------------------------------
# Cutting helpers
# --------------------------------------------------------------------------


def _box_x(name, x_min, x_max):
    bm = bmesh.new()
    mesh.add_box(
        bm,
        ((x_min + x_max) / 2.0, 0.0, 0.0),
        (x_max - x_min, 2.0 * _BIG, 2.0 * _BIG),
    )
    return mesh.mesh_object(name, bm)


def _box_y(name, y_min, y_max):
    bm = bmesh.new()
    mesh.add_box(
        bm,
        (0.0, (y_min + y_max) / 2.0, 0.0),
        (2.0 * _BIG, y_max - y_min, 2.0 * _BIG),
    )
    return mesh.mesh_object(name, bm)


def _x_extent(obj):
    """X extent of a mesh in its local (assembly) coordinates."""

    xs = [vertex.co.x for vertex in obj.data.vertices]
    return max(xs) - min(xs)


def _check_piece(name, piece):
    mesh.check_mesh(name, piece, strict=True)
    extent = _x_extent(piece)
    if extent > SPLIT_CASE_MAX_WIDTH + 1e-6:
        raise ValueError(
            f"{name} is {extent:.1f} mm wide, wider than the "
            f"{SPLIT_CASE_MAX_WIDTH:.0f} mm target; the 1:2:1 split does not "
            "make this case printable in one direction"
        )


# --------------------------------------------------------------------------
# Public entry point
# --------------------------------------------------------------------------


def split_case_edges(edges, metrics, name="case_edges"):
    """Cut a built edge ring into the four dovetailed pieces.

    Returns ``[(suffix, object), ...]`` in :data:`PIECE_SUFFIXES` order.  The
    source ``edges`` object is left untouched.  Every piece is validated
    strictly; a failed split raises before anything is exported.
    """

    west_cut, east_cut = split_positions(metrics.width)
    print(
        f"{name}: splitting into four dovetailed pieces at "
        f"x = {west_cut:.2f} and {east_cut:.2f} mm"
    )

    west_plug_north, west_slot_north = _joint_solids(
        metrics, west_cut, -1.0, "north"
    )
    west_plug_south, west_slot_south = _joint_solids(
        metrics, west_cut, -1.0, "south"
    )
    east_plug_north, east_slot_north = _joint_solids(
        metrics, east_cut, 1.0, "north"
    )
    east_plug_south, east_slot_south = _joint_solids(
        metrics, east_cut, 1.0, "south"
    )

    west_face = west_cut - SEAM_GAP / 2.0
    east_face = east_cut + SEAM_GAP / 2.0
    west_middle_face = west_cut + SEAM_GAP / 2.0
    east_middle_face = east_cut - SEAM_GAP / 2.0

    west_piece = mesh.duplicate_object(edges, f"{name}_west")
    mesh.difference(west_piece, _box_x(f"{name}_west_cut", west_face, _BIG))
    mesh.difference(west_piece, west_slot_north)
    mesh.difference(west_piece, west_slot_south)

    east_piece = mesh.duplicate_object(edges, f"{name}_east")
    mesh.difference(east_piece, _box_x(f"{name}_east_cut", -_BIG, east_face))
    mesh.difference(east_piece, east_slot_north)
    mesh.difference(east_piece, east_slot_south)

    middle = mesh.duplicate_object(edges, f"{name}_middle")
    mesh.difference(
        middle, _box_x(f"{name}_west_middle_cut", -_BIG, west_middle_face)
    )
    mesh.difference(
        middle, _box_x(f"{name}_east_middle_cut", east_middle_face, _BIG)
    )
    for plug in (
        west_plug_north,
        west_plug_south,
        east_plug_north,
        east_plug_south,
    ):
        mesh.union(middle, plug)

    # The middle slab is two disconnected rails; a cut through the open plate
    # pocket / bay at y = 0 separates them without touching any material.
    north_piece = mesh.duplicate_object(middle, f"{name}_north")
    mesh.difference(north_piece, _box_y(f"{name}_north_cut", -_BIG, 0.0))
    south_piece = mesh.duplicate_object(middle, f"{name}_south")
    mesh.difference(south_piece, _box_y(f"{name}_south_cut", 0.0, _BIG))

    # ``middle`` is only an intermediate: it still contains both rails, so it
    # must not be rendered next to the two printed pieces above.
    middle.hide_render = True

    pieces = [
        ("west", west_piece),
        ("east", east_piece),
        ("north", north_piece),
        ("south", south_piece),
    ]
    for suffix, piece in pieces:
        _check_piece(f"{name}_{suffix}", piece)

    print(
        f"{name}: four pieces, widest {max(_x_extent(p) for _, p in pieces):.2f} mm"
    )
    return pieces
