"""Shared command-line interface and output-path handling.

The options are shared by every flat keyboard entry point.  Blender consumes
everything before ``--``; only the arguments after it are parsed here.
"""

from __future__ import annotations

import argparse
import os
import sys

import layout


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
        help="case outer wall thickness (default: 7 mm)",
    )
    parser.add_argument(
        "--compartment-ramp",
        type=float,
        metavar="MM",
        help="horizontal run of the slanted case edge up to the raised "
        "compartment (default: 20 mm); 0 gives a vertical step",
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
        help="base STL path; case and cover get _case/_cover suffixes "
        "(default: build/<name>_top_plate.stl next to the repository); "
        "split mode writes _top_half/_bottom_half instead",
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


def parse_args(argv=None, description=None):
    """Parse the script arguments (everything after Blender's ``--``)."""

    parser = argument_parser(description)
    return parser.parse_args(script_arguments(argv))


def output_paths(base_path, plate_default, case_default, cover_default):
    """Resolve the three part paths for one keyboard.

    ``--output`` (``base_path``) names the top plate STL; the case and cover
    reuse its stem with ``_case`` and ``_cover`` suffixes, so generating
    variants with different paddings/facings never overwrites another variant.
    """

    if base_path is None:
        return plate_default, case_default, cover_default

    stem, extension = os.path.splitext(base_path)
    if not extension:
        extension = ".stl"
        base_path = f"{base_path}{extension}"
    return base_path, f"{stem}_case{extension}", f"{stem}_cover{extension}"


def default_paths(project_dir, name):
    """Default output paths under the repository's ``build/`` directory."""

    build_dir = os.path.join(project_dir, "build")
    return (
        os.path.join(build_dir, f"{name}_top_plate.stl"),
        os.path.join(build_dir, f"{name}_case.stl"),
        os.path.join(build_dir, f"{name}_cover.stl"),
    )


# Backwards-compatible names for older entry points.
parse_plate_args = parse_args
plate_argument_parser = argument_parser
