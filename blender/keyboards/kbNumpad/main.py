"""kbNumpad: a 17 key numeric keypad, 4u x 5u.

Generates the five printed parts into the repository ``build/`` directory:

    build/kbNumpad/top_plate/top_plate.stl
    build/kbNumpad/case/case_bottom.stl
    build/kbNumpad/case/case_edges.stl
    build/kbNumpad/cover/cover.stl
    build/kbNumpad/tilt/tilt.stl

The tall keys (plus and Enter are 2u high) get a single switch cutout at the
centre of their 2u footprint, exactly like the 1u keys.

Run headlessly with:
    blender --background --python keyboards/kbNumpad/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kbNumpad/main.py -- \
        --led-facing south --horizontal 8 --vertical 4 --output build/out.stl

``--output`` is an explicit flat override: it names the top plate STL and the
case parts, cover and tilt bar reuse its stem with _case_bottom, _case_edges,
_cover and _tilt suffixes.  Without it the files go to the per-keyboard
``top_plate``, ``case``, ``cover`` and ``tilt`` directories shown above.
"""

import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
KEYBOARDS_DIR = os.path.dirname(SCRIPT_DIR)
PROJECT_DIR = os.path.dirname(KEYBOARDS_DIR)
sys.path.insert(0, KEYBOARDS_DIR)

import cli
import generate
import layout

NAME = "kbNumpad"

# (x, y, width, height) in keyboard units; x/y is the switch centre.
# y = 0 is the NumLock row and y decreases towards the bottom row.
NUMPAD_KEYS = [
    # NumLock, /, *, -
    (0.5, 0.0, 1.0, 1.0),
    (1.5, 0.0, 1.0, 1.0),
    (2.5, 0.0, 1.0, 1.0),
    (3.5, 0.0, 1.0, 1.0),
    # 7, 8, 9 and the tall + (2u high, centred between rows 1 and 2)
    (0.5, -1.0, 1.0, 1.0),
    (1.5, -1.0, 1.0, 1.0),
    (2.5, -1.0, 1.0, 1.0),
    (3.5, -1.5, 1.0, 2.0),
    # 4, 5, 6
    (0.5, -2.0, 1.0, 1.0),
    (1.5, -2.0, 1.0, 1.0),
    (2.5, -2.0, 1.0, 1.0),
    # 1, 2, 3 and the tall Enter (2u high, rows 3 to 5)
    (0.5, -3.0, 1.0, 1.0),
    (1.5, -3.0, 1.0, 1.0),
    (2.5, -3.0, 1.0, 1.0),
    (3.5, -3.5, 1.0, 2.0),
    # 0 (2u wide) and .
    (1.0, -4.0, 2.0, 1.0),
    (2.5, -4.0, 1.0, 1.0),
]

KEYS = layout.keys_from_units(NUMPAD_KEYS)


def main(argv=None):
    args = cli.parse_args(
        argv, description=f"kbNumpad ({len(KEYS)} keys): top plate, split case and cover"
    )
    params = cli.params_from_args(args)
    plate_path, case_bottom_path, case_edges_path, cover_path, tilt_path = (
        cli.output_paths(
            args.output, *cli.default_paths(PROJECT_DIR, NAME)
        )
    )

    spec = generate.KeyboardSpec(
        name=NAME,
        keys=KEYS,
        plate_path=plate_path,
        case_bottom_path=case_bottom_path,
        case_edges_path=case_edges_path,
        cover_path=cover_path,
        tilt_path=tilt_path,
        params=params,
        compartment_height=args.compartment_height,
        compartment_ramp=args.compartment_ramp,
        wall=args.wall,
        compartment_depth=args.compartment_depth,
        split_plate=args.split_plate,
        options=cli.mounting_from_args(args),
    )
    generate.generate_keyboard(spec)
    return 0


if __name__ == "__main__":
    sys.exit(main())
