"""Deprecated compatibility facade for the old shared parts module.

New code should import the focused modules directly:

* :mod:`layout` for keys, margins and bounds,
* :mod:`hardware` for MX/socket/LED cutouts,
* :mod:`plate` for the flat top plate,
* :mod:`export` for scene reset and STL export,
* :mod:`cli` for command-line handling.

This file exists only so older scripts that imported ``keyboard_parts`` keep
working.
"""

from cli import (  # noqa: F401
    output_paths,
    params_from_args,
    parse_plate_args,
    plate_argument_parser,
    script_arguments,
)
from export import export_stl, reset_scene  # noqa: F401
from hardware import (  # noqa: F401
    CLIP_RELIEF_DEPTH,
    EPS,
    LED_LIGHT_HOLE_DIAMETER,
    LED_OFFSET,
    LED_OFFSET_Y,
    LED_POCKET,
    LED_POCKET_DEPTH,
    MX_CENTER_POST_DIAMETER,
    MX_LEG_HOLE_DIAMETER,
    MX_LEG_POSITIONS,
    MX_PIN_HOLE_DIAMETER,
    MX_PIN_POSITIONS,
    MX_POCKET,
    MX_POCKET_CLIP_X,
    MX_POCKET_CLIP_Y,
    MX_POCKET_DEPTH,
    PLATE_THICKNESS,
    SOCKET_POCKET_DEPTH,
    SOCKET_POCKET_LENGTH,
    SOCKET_POCKET_WIDTH,
    WEB_THICKNESS,
)
from layout import (  # noqa: F401
    LED_FACING_NORTH,
    LED_FACING_SOUTH,
    PITCH,
    PLATE_MARGIN,
    Key,
    PlateParams,
    keys_from_rows,
    keys_from_units,
    layout_bounds,
)
from plate import build_top_plate  # noqa: F401
