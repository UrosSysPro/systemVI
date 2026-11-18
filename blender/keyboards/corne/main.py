"""corne: placeholder for a standard Corne split keyboard.

Planned target: a standard 3x6 + 3 thumb-key Corne, generated as two mirrored
halves.  The flat-plate modules will be reused for each half, while the layout
will describe the per-half key positions and column stagger.  The split case
will need a new case/cover pair; the shared switch, LED and hotswap cutouts
from the hardware module are intended to be reused unchanged.

Not implemented yet.  Running this script exits with status 1 and writes
nothing.

Run headlessly with:
    blender --background --python keyboards/corne/main.py
"""

import sys


def main():
    print("corne: not implemented yet", file=sys.stderr)
    return 1


if __name__ == "__main__":
    sys.exit(main())
