"""Orchestration for the flat rectangular keyboards.

A :class:`KeyboardSpec` bundles the layout, parameters, output paths and the
optional raised compartment.  ``generate_keyboard`` validates every dimension
before building anything, builds all three parts, then exports them, so a bad
option cannot leave a half-written variant behind.

Split and curved boards will have their own specs/builders, but can reuse the
same layout, hardware and mesh modules.
"""

from __future__ import annotations

import math
from dataclasses import dataclass, field

import case
import cover
import export
import layout
import plate


@dataclass
class KeyboardSpec:
    """Everything needed to generate one flat keyboard's three parts."""

    name: str
    keys: list[layout.Key]
    plate_path: str
    case_path: str
    cover_path: str
    params: layout.PlateParams = field(default_factory=layout.PlateParams)
    compartment_height: float | None = None
    compartment_ramp: float | None = None


def _print_dimensions(label, obj):
    dimensions = tuple(round(value, 2) for value in obj.dimensions)
    print(
        f"{label}: "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )


def generate_keyboard(spec):
    """Build and export the plate, case and cover described by ``spec``."""

    if spec.compartment_ramp is not None and (
        not math.isfinite(spec.compartment_ramp) or spec.compartment_ramp < 0.0
    ):
        raise ValueError(
            "compartment_ramp must be a non-negative number, "
            f"got {spec.compartment_ramp}"
        )

    # Validate before resetting the scene or writing any file.
    case.case_metrics(
        spec.keys,
        spec.params,
        compartment_height=spec.compartment_height,
    )

    export.reset_scene()

    top_plate = plate.build_top_plate(
        spec.keys,
        name=f"{spec.name}_top_plate",
        params=spec.params,
    )
    case_obj = case.build_case(
        spec.keys,
        name=f"{spec.name}_case",
        params=spec.params,
        compartment_height=spec.compartment_height,
        compartment_ramp=spec.compartment_ramp,
    )
    cover_obj = cover.build_cover(
        spec.keys,
        name=f"{spec.name}_cover",
        params=spec.params,
        compartment_height=spec.compartment_height,
    )

    _print_dimensions(f"{spec.name}: plate", top_plate)
    _print_dimensions(f"{spec.name}: case", case_obj)
    _print_dimensions(f"{spec.name}: cover", cover_obj)

    export.export_stl(top_plate, spec.plate_path)
    export.export_stl(case_obj, spec.case_path)
    export.export_stl(cover_obj, spec.cover_path)

    return top_plate, case_obj, cover_obj
