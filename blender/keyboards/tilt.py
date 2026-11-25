"""Removable rear tilt bar for raising the back of the keyboard.

The bar is a wide 17.5 mm-deep, 10 mm-tall prism (80% of the case width) with a
45-degree bevel on both long bottom edges.  Two wide locating pins stand up from
its top face and enter blind rectangular sockets in the bottom tray.  Six
6 x 3 x 3 mm magnets are embedded in the bar's top face; the tray has matching
magnets in its underside.

The part is built in its printing orientation: the bottom face is at z = 0, the
pins rise above the 10 mm body and the magnet pockets open at the top face.  In
the assembly the bar is lowered by its height and moved to ``plan.mount_y``, so
its top face meets the case bottom at z = 0.

All dimensions are millimetres.
"""

from __future__ import annotations

import bmesh

import mesh

PIN_EMBED = 0.5             # pin runs into the bar body for a clean union
PIN_TOP_CHAMFER = 0.4       # lead-in around the top of each locating pin


def _pin_solid(pin, plan, name):
    """One locating pin with a small chamfer on its top perimeter."""

    z_bottom = plan.height - PIN_EMBED
    z_top = plan.height + pin.height
    bm = bmesh.new()
    mesh.add_box(
        bm,
        (
            pin.centre[0],
            pin.centre[1],
            (z_bottom + z_top) / 2.0,
        ),
        (
            pin.width,
            pin.depth,
            z_top - z_bottom,
        ),
    )
    top_edges = [
        edge
        for edge in bm.edges
        if all(abs(vertex.co.z - z_top) <= 1e-6 for vertex in edge.verts)
    ]
    if top_edges:
        bmesh.ops.bevel(
            bm,
            geom=top_edges,
            offset=PIN_TOP_CHAMFER,
            offset_type="OFFSET",
            segments=1,
            profile=0.5,
            affect="EDGES",
            clamp_overlap=True,
        )
    return mesh.mesh_object(name, bm)


def build_tilt_bar(plan, name="tilt"):
    """Build the tilt bar, its two locating pins and the magnet pockets."""

    half_w = plan.width / 2.0
    half_d = plan.depth / 2.0
    profile = (
        (-half_d, plan.height),
        (half_d, plan.height),
        (half_d, plan.bevel),
        (half_d - plan.bevel, 0.0),
        (-half_d + plan.bevel, 0.0),
        (-half_d, plan.bevel),
    )

    bm = bmesh.new()
    mesh.add_prism_x(bm, profile, -half_w, half_w)
    bar = mesh.mesh_object(name, bm)

    for index, pin in enumerate(plan.pins):
        mesh.union(bar, _pin_solid(pin, plan, f"{name}_pin_{index}"))

    pockets = bmesh.new()
    z_top = plan.height + PIN_EMBED
    z_bottom = plan.height - plan.magnet_pocket_depth
    for magnet in plan.magnets:
        mesh.add_box(
            pockets,
            (
                magnet.centre[0],
                magnet.centre[1],
                (z_bottom + z_top) / 2.0,
            ),
            (
                magnet.size[0],
                magnet.size[1],
                z_top - z_bottom,
            ),
        )
    mesh.difference(bar, mesh.mesh_object(f"{name}_magnet_pockets", pockets))

    mesh.check_mesh(name, bar, strict=True)
    return bar
