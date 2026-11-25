"""Orchestration for the flat rectangular keyboards.

A :class:`KeyboardSpec` bundles the layout, parameters, output paths and the
optional raised compartment.  ``generate_keyboard`` validates every dimension
before building anything, builds the plate, the two split case parts, the cover
and the rear tilt bar, then exports them, so a bad option cannot leave a
half-written variant behind.

Split and curved boards will have their own specs/builders, but can reuse the
same layout, hardware and mesh modules.
"""

from __future__ import annotations

import math
import os
from dataclasses import dataclass, field

import case
import case_split
import cover
import export
import layout
import mounting
import plate
import tilt


@dataclass
class KeyboardSpec:
    """Everything needed to generate one flat keyboard's five parts."""

    name: str
    keys: list[layout.Key]
    plate_path: str
    case_bottom_path: str
    case_edges_path: str
    cover_path: str
    tilt_path: str
    params: layout.PlateParams = field(default_factory=layout.PlateParams)
    compartment_height: float | None = None
    compartment_ramp: float | None = None
    wall: float | None = None
    compartment_depth: float | None = None
    split_plate: bool = True
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
    metrics = case.case_metrics(
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
    case_bottom_pieces = None
    if case.should_split_bottom(metrics):
        case_bottom_pieces = case.split_case_bottom(
            case_bottom,
            metrics,
            name=f"{spec.name}_case_bottom",
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
    tilt_obj = tilt.build_tilt_bar(
        metrics.tilt,
        name=f"{spec.name}_tilt",
    )

    case_edge_pieces = None
    if case_split.should_split(metrics):
        case_edge_pieces = case_split.split_case_edges(
            case_edges,
            metrics,
            name=f"{spec.name}_case_edges",
        )

    if split_halves is None:
        _print_dimensions(f"{spec.name}: plate", top_plate)
    else:
        top_half, bottom_half = split_halves
        _print_dimensions(f"{spec.name}: top half", top_half)
        _print_dimensions(f"{spec.name}: bottom half", bottom_half)
    if case_bottom_pieces is None:
        _print_dimensions(f"{spec.name}: case bottom", case_bottom)
    else:
        for suffix, piece in case_bottom_pieces:
            _print_dimensions(f"{spec.name}: case bottom {suffix}", piece)
    if case_edge_pieces is None:
        _print_dimensions(f"{spec.name}: case edges", case_edges)
    else:
        for suffix, piece in case_edge_pieces:
            _print_dimensions(f"{spec.name}: case edges {suffix}", piece)
    _print_dimensions(f"{spec.name}: cover", cover_obj)
    _print_dimensions(f"{spec.name}: tilt bar", tilt_obj)
    contact_y = (
        metrics.tilt.mount_y
        - metrics.tilt.depth / 2.0
        + metrics.tilt.bevel
    )
    run = contact_y - metrics.south
    if run > 0.0:
        angle = math.degrees(math.atan2(metrics.tilt.height, run))
        print(
            f"{spec.name}: tilt bar rear lift {metrics.tilt.height:.1f} mm "
            f"(~{angle:.1f} deg typing angle)"
        )

    if split_halves is None:
        export.export_stl(top_plate, spec.plate_path)
    else:
        top_path, bottom_path = _plate_split_paths(spec.plate_path)
        export.export_stl(split_halves[0], top_path)
        export.export_stl(split_halves[1], bottom_path)
    if case_bottom_pieces is None:
        export.export_stl(case_bottom, spec.case_bottom_path)
    else:
        for suffix, piece in case_bottom_pieces:
            export.export_stl(
                piece,
                case.bottom_piece_path(spec.case_bottom_path, suffix),
            )
    if case_edge_pieces is None:
        export.export_stl(case_edges, spec.case_edges_path)
    else:
        for suffix, piece in case_edge_pieces:
            export.export_stl(
                piece,
                case_split.piece_path(spec.case_edges_path, suffix),
            )
    export.export_stl(cover_obj, spec.cover_path)
    export.export_stl(tilt_obj, spec.tilt_path)

    # Keep each output mode self-contained: a normal run must not leave the
    # split halves behind, and a split run must not leave the single plate,
    # bottom tray or edge ring behind.
    stale = []
    if split_halves is None:
        stale.extend(_plate_split_paths(spec.plate_path))
    else:
        stale.append(spec.plate_path)
    if case_bottom_pieces is None:
        stale.extend(
            case.bottom_piece_path(spec.case_bottom_path, suffix)
            for suffix in case.BOTTOM_PIECE_SUFFIXES
        )
    else:
        stale.append(spec.case_bottom_path)
    if case_edge_pieces is None:
        stale.extend(
            case_split.piece_path(spec.case_edges_path, suffix)
            for suffix in case_split.PIECE_SUFFIXES
        )
    else:
        stale.append(spec.case_edges_path)
    for path in stale:
        if os.path.isfile(path):
            os.remove(path)
            print(f"Removed stale {path}")

    return top_plate, case_bottom, case_edges, cover_obj, tilt_obj
