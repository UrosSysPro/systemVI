"""dactyl: placeholder for a curved Dactyl-style split keyboard.

Planned target: one configurable Dactyl variant, initially a Dactyl Manuform
style (for example 5x6 with a 3-key thumb cluster per half).  Unlike the flat
boards, the key wells are curved and rotated in 3D, so this will use a new
curved builder rather than the flat-plate builder.  It will still reuse the
shared switch, LED and hotswap cutout dimensions from the hardware module,
applied through per-key 3D transforms.

Not implemented yet.  Running this script exits with status 1 and writes
nothing.

Run headlessly with:
    blender --background --python keyboards/dactyl/main.py
"""

import sys


def main():
    print("dactyl: not implemented yet", file=sys.stderr)
    return 1


if __name__ == "__main__":
    sys.exit(main())
