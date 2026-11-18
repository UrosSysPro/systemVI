"""Deprecated compatibility facade for the old case/cover module.

New code should import :mod:`case` for the case and :mod:`cover` for the lid.
This file exists only so older scripts that imported ``keyboard_case`` keep
working.
"""

from case import (  # noqa: F401
    CASE_HEIGHT,
    CHAMFER,
    COMPARTMENT_DEPTH,
    COMPARTMENT_HEIGHT,
    COMPARTMENT_RAMP,
    CORNER_RADIUS,
    CORNER_SEGMENTS,
    COVER_WALL,
    DEFAULT_COMPARTMENT_HEIGHT,
    FLOOR,
    LID_CLEARANCE,
    LID_LEDGE,
    LID_THICKNESS,
    PLATE_CLEARANCE,
    PLATE_TOP,
    USB_PORT_FROM_LEFT,
    USB_PORT_HEIGHT,
    USB_PORT_WIDTH,
    WALL,
    CaseMetrics,
    build_case,
    case_metrics,
)
from cover import build_cover  # noqa: F401
