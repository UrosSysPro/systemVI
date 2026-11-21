"""Shared command-line interface and output-path handling.

The options are shared by every flat keyboard entry point.  Blender consumes
everything before ``--``; only the arguments after it are parsed here.
"""

from __future__ import annotations

import argparse
import os
import sys

import layout
import mounting


def script_arguments(argv=None):
    """Return the script's own arguments.

    Blender consumes everything up to ``--``; anything after it is meant for
    the ``--python`` script.  ``argv`` can be passed explicitly for testing.
    """

    if argv is not None:
        return list(argv)

    raw = sys.argv
    if "--" in raw:
        return raw[raw.index("--") + 1 :]
    return []


def argument_parser(description=None):
    """Argparse parser with the shared flat-keyboard options."""

    parser = argparse.ArgumentParser(description=description)
    parser.add_argument(
        "--led-facing",
        choices=(layout.LED_FACING_NORTH, layout.LED_FACING_SOUTH),
        default=layout.LED_FACING_NORTH,
        help="switch orientation, south flips the whole switch footprint "
        "(default: %(default)s)",
    )

    padding = parser.add_argument_group(
        "padding (mm)",
        "The four sides default to PLATE_MARGIN (2 mm).  Later settings win: "
        "--all, then --horizontal/--vertical, then the individual sides.",
    )
    padding.add_argument(
        "--all",
        "--padding-all",
        dest="padding_all",
        type=float,
        metavar="MM",
        help="set all four sides",
    )
    padding.add_argument(
        "--horizontal",
        "--padding-horizontal",
        dest="padding_horizontal",
        type=float,
        metavar="MM",
        help="set left and right",
    )
    padding.add_argument(
        "--vertical",
        "--padding-vertical",
        dest="padding_vertical",
        type=float,
        metavar="MM",
        help="set top and bottom",
    )
    padding.add_argument(
        "--padding-left", dest="padding_left", type=float, metavar="MM"
    )
    padding.add_argument(
        "--padding-right", dest="padding_right", type=float, metavar="MM"
    )
    padding.add_argument(
        "--padding-top", dest="padding_top", type=float, metavar="MM"
    )
    padding.add_argument(
        "--padding-bottom", dest="padding_bottom", type=float, metavar="MM"
    )

    parser.add_argument(
        "--compartment-height",
        type=float,
        metavar="MM",
        help="raised top compartment height; must be at least the case rim "
        "height (default: 27.5 mm, raised)",
    )
    parser.add_argument(
        "--compartment-depth",
        type=float,
        metavar="MM",
        help="internal north/south depth of the electronics bay "
        "(default: 17.5 mm)",
    )
    parser.add_argument(
        "--wall",
        type=float,
        metavar="MM",
        help="case outer wall thickness (default: 7 mm; the split case needs "
        "at least 7 mm)",
    )
    parser.add_argument(
        "--compartment-ramp",
        type=float,
        metavar="MM",
        help="horizontal run of the slanted case edge up to the raised "
        "compartment (default: 20 mm); 0 gives a vertical step",
    )

    hardware_group = parser.add_argument_group(
        "cover magnets and wire routing (mm)",
        "The split case clamps the plate tabs with M2 screws; the cover is "
        "held by magnets on 45-degree bay shelves and the case floor is "
        "recessed for wires.",
    )
    defaults = mounting.MountingOptions()
    hardware_group.add_argument(
        "--magnet-width",
        type=float,
        default=defaults.magnet_width,
        metavar="MM",
        help="magnet long side, along the edge (default: %(default)s)",
    )
    hardware_group.add_argument(
        "--magnet-length",
        type=float,
        default=defaults.magnet_length,
        metavar="MM",
        help="magnet short side, across the edge (default: %(default)s)",
    )
    hardware_group.add_argument(
        "--magnet-thickness",
        type=float,
        default=defaults.magnet_thickness,
        metavar="MM",
        help="magnet thickness / vertical size; sets the local cover boss "
        "height (default: %(default)s)",
    )
    hardware_group.add_argument(
        "--magnet-spacing",
        type=float,
        default=defaults.magnet_spacing,
        metavar="MM",
        help="target spacing for the adaptive top-edge magnet row "
        "(default: %(default)s)",
    )
    hardware_group.add_argument(
        "--wire-gap",
        type=float,
        default=defaults.wire_gap,
        metavar="MM",
        help="recess depth in the plate pocket floor for wires "
        "(default: %(default)s)",
    )

    parser.add_argument(
        "--split-plate",
        action="store_true",
        help="export the top plate as separate top and bottom halves instead "
        "of one STL; case and cover are unchanged",
    )
    parser.add_argument(
        "--output",
        metavar="PATH",
        help="explicit flat base STL path; the case parts and cover get "
        "_case_bottom/_case_edges/_cover suffixes.  By default each keyboard "
        "writes build/<name>/top_plate/top_plate.stl, "
        "build/<name>/case/case_bottom.stl, build/<name>/case/case_edges.stl "
        "and build/<name>/cover/cover.stl; --split-plate writes "
        "_top_half/_bottom_half instead, and a wide case writes "
        "_case_edges_west/_east/_north/_south in the case directory",
    )
    return parser


def params_from_args(args):
    """Build a :class:`layout.PlateParams` from parsed CLI arguments."""

    base = layout.PLATE_MARGIN if args.padding_all is None else args.padding_all
    left = right = top = bottom = base

    if args.padding_horizontal is not None:
        left = right = args.padding_horizontal
    if args.padding_vertical is not None:
        top = bottom = args.padding_vertical

    if args.padding_left is not None:
        left = args.padding_left
    if args.padding_right is not None:
        right = args.padding_right
    if args.padding_top is not None:
        top = args.padding_top
    if args.padding_bottom is not None:
        bottom = args.padding_bottom

    try:
        return layout.PlateParams(
            margin_left=left,
            margin_right=right,
            margin_top=top,
            margin_bottom=bottom,
            led_facing=args.led_facing,
        )
    except ValueError as exc:
        raise SystemExit(f"error: {exc}") from exc


def mounting_from_args(args):
    """Build the mounting options from parsed CLI arguments."""

    options = mounting.MountingOptions(
        magnet_width=args.magnet_width,
        magnet_length=args.magnet_length,
        magnet_thickness=args.magnet_thickness,
        magnet_spacing=args.magnet_spacing,
        wire_gap=args.wire_gap,
    )
    try:
        options.validate()
    except ValueError as exc:
        raise SystemExit(f"error: {exc}") from exc
    return options


def parse_args(argv=None, description=None):
    """Parse the script arguments (everything after Blender's ``--``)."""

    parser = argument_parser(description)
    return parser.parse_args(script_arguments(argv))


def output_paths(
    base_path,
    plate_default,
    case_bottom_default,
    case_edges_default,
    cover_default,
):
    """Resolve the four part paths for one keyboard.

    ``--output`` (``base_path``) names the top plate STL; the two case parts
    and cover reuse its stem with ``_case_bottom``, ``_case_edges`` and
    ``_cover`` suffixes, so generating variants with different
    paddings/facings never overwrites another variant.
    """

    if base_path is None:
        return (
            plate_default,
            case_bottom_default,
            case_edges_default,
            cover_default,
        )

    stem, extension = os.path.splitext(base_path)
    if not extension:
        extension = ".stl"
        base_path = f"{base_path}{extension}"
    return (
        base_path,
        f"{stem}_case_bottom{extension}",
        f"{stem}_case_edges{extension}",
        f"{stem}_cover{extension}",
    )


def default_paths(project_dir, name):
    """Default output paths under ``build/<name>/``.

    Each keyboard gets one directory per part group::

        build/<name>/top_plate/top_plate.stl
        build/<name>/case/case_bottom.stl
        build/<name>/case/case_edges.stl
        build/<name>/cover/cover.stl

    A wide case replaces ``case_edges.stl`` with the four ``case_edges_*``
    pieces in the same directory, and ``--split-plate`` replaces
    ``top_plate.stl`` with ``top_plate_top_half.stl`` and
    ``top_plate_bottom_half.stl``.
    """

    base_dir = os.path.join(project_dir, "build", name)
    return (
        os.path.join(base_dir, "top_plate", "top_plate.stl"),
        os.path.join(base_dir, "case", "case_bottom.stl"),
        os.path.join(base_dir, "case", "case_edges.stl"),
        os.path.join(base_dir, "cover", "cover.stl"),
    )


# Backwards-compatible names for older entry points.
parse_plate_args = parse_args
plate_argument_parser = argument_parser
