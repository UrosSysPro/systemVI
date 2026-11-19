"""kb75: a 75% style keyboard, 83 keys, 16u x 6u.

Generates the three printed parts into the repository ``build/`` directory:

    build/kb75_top_plate.stl
    build/kb75_case.stl
    build/kb75_cover.stl

The plate cutouts, case and cover come from the shared keyboard modules.

Run headlessly with:
    blender --background --python keyboards/kb75/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kb75/main.py -- \
        --led-facing south --horizontal 8 --vertical 4 --output build/out.stl

``--output`` names the top plate; the case and cover reuse its stem with
_case and _cover suffixes.
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

NAME = "kb75"

# 75% layout: 16 keyboard units wide, six rows.
# A row is a list of key widths in units; None inserts a 1u gap.
LAYOUT_ROWS = [
    # Esc, F1..F12, gap, PrtSc, Ins
    [1.0] * 13 + [None] + [1.0, 1.0],
    # `, 1..0, -, =, Backspace (2u), Del
    [1.0] * 13 + [2.0, 1.0],
    # Tab (1.5u), Q..P, [, ], \ (1.5u), Home
    [1.5] + [1.0] * 12 + [1.5, 1.0],
    # Caps (1.75u), A..L, ;, ', Enter (2.25u), PgUp
    [1.75] + [1.0] * 11 + [2.25, 1.0],
    # LShift (2.25u), Z../, RShift (1.75u), Up, PgDn
    [2.25] + [1.0] * 10 + [1.75, 1.0, 1.0],
    # Ctrl, Win, Alt, Space (6u), Alt, Fn, Ctrl, Left, Down, Right
    [1.25, 1.25, 1.25, 6.0, 1.25, 1.0, 1.0, 1.0, 1.0, 1.0],
]

KEYS = layout.keys_from_rows(LAYOUT_ROWS)


def main(argv=None):
    args = cli.parse_args(
        argv, description=f"kb75 ({len(KEYS)} keys): top plate, case and cover"
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
        options=cli.mounting_from_args(args),
    )
    generate.generate_keyboard(spec)
    return 0


if __name__ == "__main__":
    sys.exit(main())
