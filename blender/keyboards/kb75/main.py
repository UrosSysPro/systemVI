"""kb75 top plate: a 75% style keyboard, 80 keys, 16u x 6u.

Builds the thick top plate (switch, RGB LED and hotswap socket cutouts are
provided by keyboards/keyboard_parts.py) and writes kb75_top_plate.stl next
to this file.

Run headlessly with:
    blender --background --python keyboards/kb75/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kb75/main.py -- \
        --led-facing south --horizontal 8 --vertical 4 --output out.stl
"""

import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
KEYBOARDS_DIR = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, KEYBOARDS_DIR)

import keyboard_parts as kb

OUTPUT_PATH = os.path.join(SCRIPT_DIR, "kb75_top_plate.stl")

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
    args = kb.parse_plate_args(argv, description="kb75 top plate (80 keys)")
    params = kb.params_from_args(args)
    output_path = args.output or OUTPUT_PATH

    keys = kb.keys_from_rows(LAYOUT_ROWS)
    kb.generate_top_plate(keys, "kb75_top_plate", output_path, params=params)
    return 0


if __name__ == "__main__":
    sys.exit(main())
