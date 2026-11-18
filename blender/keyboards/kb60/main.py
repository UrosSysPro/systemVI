"""kb60: a standard ANSI 61-key 60% keyboard, 15u x 5u.

Generates the three printed parts into the repository ``build/`` directory:

    build/kb60_top_plate.stl
    build/kb60_case.stl
    build/kb60_cover.stl

The layout is the usual 60% arrangement: Esc in the top-left, no separate
function row, no navigation cluster and no numeric keypad.  All keys are 1u
wide except Backspace (2u), Tab (1.5u), Caps (1.75u), Enter (2.25u), LShift
(2.25u), RShift (2.75u) and the bottom-row modifiers.

Run headlessly with:
    blender --background --python keyboards/kb60/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kb60/main.py -- \
        --led-facing south --horizontal 6 --output build/kb60_wide.stl

``--output`` names the top plate; the case and cover reuse its stem with
``_case`` and ``_cover`` suffixes.
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

NAME = "kb60"

# One row per width list; every row spans 15u.
LAYOUT_ROWS = [
    # Esc, 1..0, -, =, Backspace (2u)
    [1.0] * 13 + [2.0],
    # Tab (1.5u), Q..P, [, ], \ (1.5u)
    [1.5] + [1.0] * 12 + [1.5],
    # Caps (1.75u), A..L, ;, ', Enter (2.25u)
    [1.75] + [1.0] * 11 + [2.25],
    # LShift (2.25u), Z..M, , . /, RShift (2.75u)
    [2.25] + [1.0] * 10 + [2.75],
    # Ctrl, Win, Alt, Space (6.25u), Alt, Win, Menu, Ctrl
    [1.25, 1.25, 1.25, 6.25, 1.25, 1.25, 1.25, 1.25],
]

KEYS = layout.keys_from_rows(LAYOUT_ROWS)


def main(argv=None):
    args = cli.parse_args(
        argv, description=f"kb60 ({len(KEYS)} keys): top plate, case and cover"
    )
    params = cli.params_from_args(args)
    plate_path, case_path, cover_path = cli.output_paths(
        args.output, *cli.default_paths(PROJECT_DIR, NAME)
    )

    spec = generate.KeyboardSpec(
        name=NAME,
        keys=KEYS,
        plate_path=plate_path,
        case_path=case_path,
        cover_path=cover_path,
        params=params,
        compartment_height=args.compartment_height,
        compartment_ramp=args.compartment_ramp,
        wall=args.wall,
        compartment_depth=args.compartment_depth,
        split_plate=args.split_plate,
    )
    generate.generate_keyboard(spec)
    return 0


if __name__ == "__main__":
    sys.exit(main())
