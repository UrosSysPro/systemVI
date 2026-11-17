"""kbNumpad: a 17 key numeric keypad, 4u x 5u.

Generates the three printed parts and writes them next to this file:
kbNumpad_top_plate.stl, kbNumpad_case.stl and kbNumpad_cover.stl.  The plate
cutouts come from keyboards/keyboard_parts.py, the case and lid from
keyboards/keyboard_case.py.

The tall keys (plus and Enter are 2u high) get a single switch cutout at the
centre of their 2u footprint, exactly like the 1u keys.

Run headlessly with:
    blender --background --python keyboards/kbNumpad/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kbNumpad/main.py -- \
        --led-facing south --horizontal 8 --vertical 4 --output out.stl

``--output`` names the top plate; the case and cover reuse its stem with
_case and _cover suffixes.
"""

import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
KEYBOARDS_DIR = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, KEYBOARDS_DIR)

import keyboard_case as kc
import keyboard_parts as kb

PLATE_PATH = os.path.join(SCRIPT_DIR, "kbNumpad_top_plate.stl")
CASE_PATH = os.path.join(SCRIPT_DIR, "kbNumpad_case.stl")
COVER_PATH = os.path.join(SCRIPT_DIR, "kbNumpad_cover.stl")

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


def main(argv=None):
    args = kb.parse_plate_args(
        argv, description="kbNumpad (17 keys): top plate, case and cover"
    )
    params = kb.params_from_args(args)
    plate_path, case_path, cover_path = kb.output_paths(
        args.output, PLATE_PATH, CASE_PATH, COVER_PATH
    )

    keys = kb.keys_from_units(NUMPAD_KEYS)
    kb.generate_top_plate(keys, "kbNumpad_top_plate", plate_path, params=params)
    kc.generate_case(
        keys,
        "kbNumpad_case",
        case_path,
        params=params,
        compartment_height=args.compartment_height,
        compartment_ramp=args.compartment_ramp,
    )
    kc.generate_cover(
        keys,
        "kbNumpad_cover",
        cover_path,
        params=params,
        compartment_height=args.compartment_height,
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
