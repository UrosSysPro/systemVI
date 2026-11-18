"""Flat top-plate builder shared by the rectangular keyboards.

The builder is deliberately layout agnostic: give it any list of
:class:`layout.Key` objects and it produces one flat slab with the shared
switch, hotswap and LED cutouts.  Split boards can call it once per half;
curved Dactyl key wells will use a separate builder that reuses
:mod:`hardware`.
"""

from __future__ import annotations

import bmesh

import hardware
import layout
import mesh
import stabilizer


def build_top_plate(keys, name="top_plate", params=None):
    """Create the flat top plate object for ``keys``, centred on the origin.

    ``params`` is a :class:`layout.PlateParams` with the four margins and the
    LED facing.  The plate bottom sits on z = 0 and its top on
    z = ``hardware.PLATE_THICKNESS``, so the exported STL drops straight onto
    the printer bed.
    """

    params = params or layout.PlateParams()
    facing = params.facing_angle()
    thickness = hardware.PLATE_THICKNESS
    bounds = layout.layout_bounds(keys, params)

    placed = [
        (
            key.x - bounds.centre_x,
            key.y - bounds.centre_y,
            key.rotation + facing,
        )
        for key in keys
    ]
    stabilizer_placed = [
        (
            key.x - bounds.centre_x,
            key.y - bounds.centre_y,
            key.rotation + facing,
            key.width,
        )
        for key in keys
    ]

    bm = bmesh.new()
    mesh.add_box(
        bm,
        (0.0, 0.0, thickness / 2.0),
        (bounds.width, bounds.height, thickness),
    )
    plate = mesh.mesh_object(name, bm)

    cutters = [
        hardware.switch_pocket_cutter(placed, thickness),
        hardware.socket_pocket_cutter(placed),
        hardware.led_pocket_cutter(placed),
        hardware.led_light_hole_cutter(placed, thickness),
        hardware.hole_cutter(placed, thickness),
    ]
    cutters.extend(stabilizer.plate_cutters(stabilizer_placed, thickness))
    for cutter in cutters:
        mesh.difference(plate, cutter)

    mesh.check_mesh(name, plate, strict=True)
    return plate
