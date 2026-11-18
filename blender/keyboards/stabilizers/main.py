"""Generate one custom 2 mm-wire stabilizer slider.

The slider is symmetric: print two per stabilized key.  The same part fits
both sides of a key; the wire spacing comes from the keycap width and is cut
into the top plate automatically by the keyboard generators.

The script also prints the wire length and bend data for standard wide-key
widths.  The wire is a flat U with two 90 degree bends; the straight span is
``2 * offset`` and each horizontal leg is ``SPAN_OFFSET_Y`` long.  Vertical
1x2u keys use the same 2u wire rotated 90 degrees, and the same slider rotated
90 degrees clockwise so its wire slot opens east.  Each slider bottom has a
solid floor with a slanted hole that feeds the wire into the internal capture
slot.

Generates:
    build/stabilizer_slider.stl

Run headlessly with:
    blender --background --python keyboards/stabilizers/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/stabilizers/main.py -- \
        --width 6.25 --output build/stabilizer_slider.stl
"""

import argparse
import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
KEYBOARDS_DIR = os.path.dirname(SCRIPT_DIR)
PROJECT_DIR = os.path.dirname(KEYBOARDS_DIR)
sys.path.insert(0, KEYBOARDS_DIR)

import cli
import export
import layout
import stabilizer

NAME = "stabilizer_slider"


def _print_wire_spec(width_units):
    spec = stabilizer.wire_spec(width_units * layout.PITCH)
    print(
        f"  {width_units:.2f}u: offset {spec.offset:.2f} mm, "
        f"span {spec.span:.2f} mm, cut ~{spec.length + 5.0:.1f} mm "
        "(including 5 mm trim allowance)"
    )


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--output",
        metavar="PATH",
        help="STL path (default: build/stabilizer_slider.stl)",
    )
    parser.add_argument(
        "--width",
        type=float,
        metavar="U",
        help="print wire data for one key width in keyboard units (e.g. 6.25)",
    )
    args = parser.parse_args(cli.script_arguments(argv))

    output_path = args.output or os.path.join(
        PROJECT_DIR, "build", f"{NAME}.stl"
    )
    output_path = os.path.abspath(output_path)

    export.reset_scene()
    slider = stabilizer.build_slider(NAME)
    dimensions = tuple(round(value, 2) for value in slider.dimensions)
    print(
        f"{NAME}: {dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )
    print("Print two sliders per stabilized key.")
    print(
        "For vertical 1x2u keys, rotate each slider 90 degrees clockwise "
        "so the wire slot opens east."
    )
    print(
        f"Wire: {stabilizer.WIRE_DIAMETER:.1f} mm solid copper, "
        f"bend radius {stabilizer.WIRE_BEND_RADIUS:.1f} mm, "
        f"horizontal leg {stabilizer.SPAN_OFFSET_Y:.1f} mm, 2 bends."
    )

    if args.width is not None:
        if args.width < 2.0:
            parser.error("--width must be at least 2u")
        _print_wire_spec(args.width)
    else:
        print("Wire lengths for standard wide keys:")
        for width_units in (2.0, 2.25, 2.75, 3.0, 6.0, 6.25, 7.0):
            _print_wire_spec(width_units)

    export.export_stl(slider, output_path)
    return 0


if __name__ == "__main__":
    sys.exit(main())
