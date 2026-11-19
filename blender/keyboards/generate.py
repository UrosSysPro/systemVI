"""Orchestration for the flat rectangular keyboards.

A :class:`KeyboardSpec` bundles the layout, parameters, output paths and the
optional raised compartment.  ``generate_keyboard`` validates every dimension
before building anything, builds the plate, the two split case parts and the
cover, then exports them, so a bad option cannot leave a half-written variant
behind.

Split and curved boards will have their own specs/builders, but can reuse the
same layout, hardware and mesh modules.
"""

from __future__ import annotations

import math
import os
from dataclasses import dataclass, field

import case
import cover
import export
import layout
import mounting
import plate


@dataclass
class KeyboardSpec:
    """Everything needed to generate one flat keyboard's four parts."""

    name: str
    keys: list[layout.Key]
    plate_path: str
    case_bottom_path: str
    case_edges_path: str
    cover_path: str
    params: layout.PlateParams = field(default_factory=layout.PlateParams)
    compartment_height: float | None = None
    compartment_ramp: float | None = None
    wall: float | None = None
    compartment_depth: float | None = None
    split_plate: bool = False
    options: mounting.MountingOptions = field(
        default_factory=mounting.MountingOptions
    )


def _plate_split_paths(plate_path):
    """Derive the two split-plate paths from the normal plate path."""

    stem, extension = os.path.splitext(plate_path)
    extension = extension or ".stl"
    return (
        f"{stem}_top_half{extension}",
        f"{stem}_bottom_half{extension}",
    )


def _print_dimensions(label, obj):
    dimensions = tuple(round(value, 2) for value in obj.dimensions)
    print(
        f"{label}: "
        f"{dimensions[0]} x {dimensions[1]} x {dimensions[2]} mm"
    )


def generate_keyboard(spec):
    """Build and export the plate, two case parts and cover for ``spec``."""

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
        wall=spec.wall,
        compartment_depth=spec.compartment_depth,
        options=spec.options,
    )

    export.reset_scene()

    top_plate = plate.build_top_plate(
        spec.keys,
        name=f"{spec.name}_top_plate",
        params=spec.params,
    )
    split_halves = None
    if spec.split_plate:
        split_halves = plate.split_top_plate(
            top_plate,
            name=f"{spec.name}_top_plate",
        )
    case_bottom = case.build_case_bottom(
        spec.keys,
        name=f"{spec.name}_case_bottom",
        params=spec.params,
        compartment_height=spec.compartment_height,
        compartment_ramp=spec.compartment_ramp,
        wall=spec.wall,
        compartment_depth=spec.compartment_depth,
        options=spec.options,
    )
    case_edges = case.build_case_edges(
        spec.keys,
        name=f"{spec.name}_case_edges",
        params=spec.params,
        compartment_height=spec.compartment_height,
        compartment_ramp=spec.compartment_ramp,
        wall=spec.wall,
        compartment_depth=spec.compartment_depth,
        options=spec.options,
    )
    cover_obj = cover.build_cover(
        spec.keys,
        name=f"{spec.name}_cover",
        params=spec.params,
        compartment_height=spec.compartment_height,
        wall=spec.wall,
        compartment_depth=spec.compartment_depth,
        options=spec.options,
    )

    if split_halves is None:
        _print_dimensions(f"{spec.name}: plate", top_plate)
    else:
        top_half, bottom_half = split_halves
        _print_dimensions(f"{spec.name}: top half", top_half)
        _print_dimensions(f"{spec.name}: bottom half", bottom_half)
    _print_dimensions(f"{spec.name}: case bottom", case_bottom)
    _print_dimensions(f"{spec.name}: case edges", case_edges)
    _print_dimensions(f"{spec.name}: cover", cover_obj)

    if split_halves is None:
        export.export_stl(top_plate, spec.plate_path)
    else:
        top_path, bottom_path = _plate_split_paths(spec.plate_path)
        export.export_stl(split_halves[0], top_path)
        export.export_stl(split_halves[1], bottom_path)
    export.export_stl(case_bottom, spec.case_bottom_path)
    export.export_stl(case_edges, spec.case_edges_path)
    export.export_stl(cover_obj, spec.cover_path)

    return top_plate, case_bottom, case_edges, cover_obj
