"""kbNumpad top plate: a 17 key numeric keypad, 4u x 5u.

Builds the thick top plate (switch, RGB LED and hotswap socket cutouts are
provided by keyboards/keyboard_parts.py) and writes kbNumpad_top_plate.stl
next to this file.

The tall keys (plus and Enter are 2u high) get a single switch cutout at the
centre of their 2u footprint, exactly like the 1u keys.

Run headlessly with:
    blender --background --python keyboards/kbNumpad/main.py
"""

import os
import sys

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
KEYBOARDS_DIR = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, KEYBOARDS_DIR)

import keyboard_parts as kb

OUTPUT_PATH = os.path.join(SCRIPT_DIR, "kbNumpad_top_plate.stl")

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


def main():
    keys = kb.keys_from_units(NUMPAD_KEYS)
    kb.generate_top_plate(keys, "kbNumpad_top_plate", OUTPUT_PATH)
    return 0


if __name__ == "__main__":
    sys.exit(main())
