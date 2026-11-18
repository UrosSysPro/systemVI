"""kb100: a full-size ANSI 104-key keyboard, 22.5u x 6u.

Generates the three printed parts into the repository ``build/`` directory:

    build/kb100_top_plate.stl
    build/kb100_case.stl
    build/kb100_cover.stl

The plate cutouts, case and cover come from the shared keyboard modules.  The
layout is a single piece: a 15u main block, a 3u navigation cluster and a 4u
numeric keypad, separated by 0.25u gaps.  The function row contains Esc,
F1-F12 and PrtSc/ScrollLock/Pause; the numeric keypad starts on the number row.

Run headlessly with:
    blender --background --python keyboards/kb100/main.py

Options go after Blender's ``--`` separator:
    blender --background --python keyboards/kb100/main.py -- \
        --led-facing south --horizontal 6 --output build/kb100_wide.stl

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

NAME = "kb100"


def _add_key(keys, centre_x, centre_y, width=1.0, height=1.0):
    """Append a key at a centre given in keyboard units."""

    keys.append(
        layout.Key(
            centre_x * layout.PITCH,
            centre_y * layout.PITCH,
            width=width * layout.PITCH,
            height=height * layout.PITCH,
        )
    )


def _add_row(keys, x_start, y, widths):
    """Append a row of keys starting at ``x_start`` units.

    ``None`` advances by one unit without adding a key.
    """

    x = x_start
    for width in widths:
        if width is None:
            x += 1.0
            continue
        _add_key(keys, x + width / 2.0, y, width)
        x += width


def build_layout():
    """Return the ANSI 104-key layout in millimetres."""

    keys = []

    # Function row: Esc, F1-F4, F5-F8, F9-F12, PrtSc, ScrollLock, Pause.
    _add_key(keys, 0.5, 0.0)
    _add_row(keys, 2.0, 0.0, [1.0] * 4)
    _add_row(keys, 6.5, 0.0, [1.0] * 4)
    _add_row(keys, 11.0, 0.0, [1.0] * 4)
    _add_row(keys, 15.25, 0.0, [1.0, 1.0, 1.0])

    # Number row: ` 1..0 - = Backspace, Insert/Home/PageUp, NumLock / * -.
    _add_row(keys, 0.0, -1.0, [1.0] * 13 + [2.0])
    _add_row(keys, 15.25, -1.0, [1.0, 1.0, 1.0])
    _add_row(keys, 18.5, -1.0, [1.0, 1.0, 1.0, 1.0])

    # Q row: Tab, Q..P, [, ], \, Delete/End/PageDown, 7 8 9 and a 2u plus.
    _add_row(keys, 0.0, -2.0, [1.5] + [1.0] * 12 + [1.5])
    _add_row(keys, 15.25, -2.0, [1.0, 1.0, 1.0])
    _add_row(keys, 18.5, -2.0, [1.0, 1.0, 1.0])
    _add_key(keys, 22.0, -2.5, 1.0, 2.0)

    # Home row: Caps, A..L, ;, ', Enter; no navigation keys; 4 5 6.
    _add_row(keys, 0.0, -3.0, [1.75] + [1.0] * 11 + [2.25])
    _add_row(keys, 18.5, -3.0, [1.0, 1.0, 1.0])

    # Shift row: LShift, Z..M, , . /, RShift; Up; 1 2 3 and a 2u Enter.
    _add_row(keys, 0.0, -4.0, [2.25] + [1.0] * 10 + [2.75])
    _add_key(keys, 16.75, -4.0)
    _add_row(keys, 18.5, -4.0, [1.0, 1.0, 1.0])
    _add_key(keys, 22.0, -4.5, 1.0, 2.0)

    # Bottom row: Ctrl Win Alt Space Alt Win Menu Ctrl; Left Down Right; 0 . .
    _add_row(
        keys,
        0.0,
        -5.0,
        [1.25, 1.25, 1.25, 6.25, 1.25, 1.25, 1.25, 1.25],
    )
    _add_row(keys, 15.25, -5.0, [1.0, 1.0, 1.0])
    _add_key(keys, 19.5, -5.0, 2.0, 1.0)
    _add_key(keys, 21.0, -5.0)

    return keys


KEYS = build_layout()


def main(argv=None):
    args = cli.parse_args(
        argv, description=f"kb100 ({len(KEYS)} keys): top plate, case and cover"
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
