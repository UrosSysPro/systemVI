"""kb75: a 75% style keyboard, 80 keys, 16u x 6u.

Generates the three printed parts and writes them next to this file:
kb75_top_plate.stl, kb75_case.stl and kb75_cover.stl.  The plate cutouts come
from keyboards/keyboard_parts.py, the case and lid from
keyboards/keyboard_case.py.

Run headlessly with:
    blender --background --python keyboards/kb75/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kb75/main.py -- \
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

PLATE_PATH = os.path.join(SCRIPT_DIR, "kb75_top_plate.stl")
CASE_PATH = os.path.join(SCRIPT_DIR, "kb75_case.stl")
COVER_PATH = os.path.join(SCRIPT_DIR, "kb75_cover.stl")

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


def main(argv=None):
    args = kb.parse_plate_args(
        argv, description="kb75 (80 keys): top plate, case and cover"
    )
    params = kb.params_from_args(args)
    plate_path, case_path, cover_path = kb.output_paths(
        args.output, PLATE_PATH, CASE_PATH, COVER_PATH
    )

    keys = kb.keys_from_rows(LAYOUT_ROWS)
    kb.generate_top_plate(keys, "kb75_top_plate", plate_path, params=params)
    kc.generate_case(keys, "kb75_case", case_path, params=params)
    kc.generate_cover(keys, "kb75_cover", cover_path, params=params)
    return 0


if __name__ == "__main__":
    sys.exit(main())
