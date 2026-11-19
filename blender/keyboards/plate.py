"""Flat top-plate builder shared by the rectangular keyboards.

The builder is deliberately layout agnostic: give it any list of
:class:`layout.Key` objects and it produces one flat slab with the shared
switch, hotswap and LED cutouts.  Split boards can call it once per half;
curved Dactyl key wells will use a separate builder that reuses
:mod:`hardware`.

The plate carries lower side tabs that are clamped between the split case's
bottom tray and edge ring.  M2 clearance holes in the tabs line up with the
counterbores in the bottom tray and the vertical heatset inserts in the edge
ring.  See :mod:`mounting` for the shared tab plan.
"""

from __future__ import annotations

import bmesh
import bpy

import hardware
import layout
import mesh
import mounting
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
            key.height,
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

    tab_plan = mounting.plate_tab_plan(bounds.width, bounds.height)
    tab_bm = bmesh.new()
    for tab in tab_plan.tabs:
        if tab.side in ("west", "east"):
            radial_centre = (
                -bounds.width / 2.0
                + (mounting.TAB_OVERLAP - mounting.TAB_DEPTH) / 2.0
            )
            if tab.side == "east":
                radial_centre = -radial_centre
            mesh.add_box(
                tab_bm,
                (radial_centre, tab.centre[1], mounting.TAB_HEIGHT / 2.0),
                (
                    mounting.TAB_DEPTH + mounting.TAB_OVERLAP,
                    tab.width,
                    mounting.TAB_HEIGHT,
                ),
            )
        else:
            radial_centre = (
                -bounds.height / 2.0
                + (mounting.TAB_OVERLAP - mounting.TAB_DEPTH) / 2.0
            )
            mesh.add_box(
                tab_bm,
                (tab.centre[0], radial_centre, mounting.TAB_HEIGHT / 2.0),
                (
                    tab.width,
                    mounting.TAB_DEPTH + mounting.TAB_OVERLAP,
                    mounting.TAB_HEIGHT,
                ),
            )
    tabs = mesh.mesh_object(f"{name}_tabs", tab_bm)
    mesh.union(plate, tabs)

    socket_body, socket_tab = hardware.socket_pocket_cutters(placed)
    cutters = [
        hardware.switch_pocket_cutter(placed, thickness),
        socket_body,
        hardware.led_pocket_cutter(placed),
        hardware.led_light_hole_cutter(placed, thickness),
        hardware.hole_cutter(placed, thickness),
        socket_tab,
    ]
    cutters.extend(stabilizer.plate_cutters(stabilizer_placed, thickness))

    screw_bm = bmesh.new()
    for tab in tab_plan.tabs:
        mesh.add_cylinder(
            screw_bm,
            tab.screw,
            mounting.SCREW_HOLE_DIAMETER / 2.0,
            -hardware.EPS,
            mounting.TAB_HEIGHT + hardware.EPS,
        )
    cutters.append(mesh.mesh_object(f"{name}_tab_holes", screw_bm))

    for cutter in cutters:
        mesh.difference(plate, cutter)

    # Exact booleans occasionally leave a tiny boundary loop at a hotswap
    # pocket corner, especially on the south-facing 100% plate.  Close only
    # sub-millimetre loops; anything larger still fails the strict check.
    mesh.fill_small_holes(name, plate)

    mesh.check_mesh(name, plate, strict=True)
    return plate


def _duplicate(obj, name):
    duplicate = bpy.data.objects.new(name, obj.data.copy())
    bpy.context.scene.collection.objects.link(duplicate)
    duplicate.location = obj.location
    duplicate.rotation_euler = obj.rotation_euler
    duplicate.scale = obj.scale
    return duplicate


def _halfspace(name, cut_z, above):
    """Large box covering one side of a horizontal cut."""

    xy_size = 1000.0
    if above:
        z_min, z_max = cut_z, cut_z + 1000.0
    else:
        z_min, z_max = cut_z - 1000.0, cut_z

    bm = bmesh.new()
    mesh.add_box(
        bm,
        (0.0, 0.0, (z_min + z_max) / 2.0),
        (xy_size, xy_size, z_max - z_min),
    )
    return mesh.mesh_object(name, bm)


def split_top_plate(plate, name="top_plate", cut_z=None):
    """Cut the plate through the middle of the switch/socket web.

    Returns two closed objects: the top half (switch pockets) and the bottom
    half (hotswap/LED pockets).  The source plate is left unchanged.  Both
    halves are validated strictly; a failed split raises without exporting.
    """

    if cut_z is None:
        # Keep the whole 4 mm fastening tab in the bottom half.  The cut is
        # still inside the switch/socket web (3.0 .. 4.5 mm).
        cut_z = max(
            mounting.TAB_HEIGHT,
            hardware.SOCKET_POCKET_DEPTH + hardware.WEB_THICKNESS / 2.0,
        )
    if not 0.0 < cut_z < hardware.PLATE_THICKNESS:
        raise ValueError(
            f"cut_z must be between 0 and {hardware.PLATE_THICKNESS} mm, "
            f"got {cut_z} mm"
        )

    print(
        f"{name}: splitting at z={cut_z} mm "
        f"(switch floor {hardware.PLATE_THICKNESS - hardware.MX_POCKET_DEPTH} mm, "
        f"socket ceiling {hardware.SOCKET_POCKET_DEPTH} mm)"
    )

    top = _duplicate(plate, f"{name}_top_half")
    bottom = _duplicate(plate, f"{name}_bottom_half")

    mesh.difference(top, _halfspace("cut_below", cut_z, above=False))
    mesh.difference(bottom, _halfspace("cut_above", cut_z, above=True))

    mesh.check_mesh(f"{name}_top_half", top, strict=True)
    mesh.check_mesh(f"{name}_bottom_half", bottom, strict=True)
    return top, bottom
