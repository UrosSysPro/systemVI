"""L-shaped cover for the north electronics bay.

The horizontal leg rests on the bay shelf; the vertical leg hangs down in front
of the top row of keys to the plate surface.  When the bay is flush, there is
no lip below the lid, so the cover is a plain box; the builder special-cases
that instead of emitting a degenerate profile.

The cover is held by rectangular magnets on 45-degree case shelves.  The lid
itself stays thin; each magnet sits in a small local boss on the underside, so
only the magnet locations are thickened.  The boss bottoms land on the shelf
tops while the rest of the cover rests on the bay shelf.
"""

from __future__ import annotations

import bmesh

import case
import hardware
import mesh
import mounting


def _boss_rect(metrics, mount):
    """Footprint of the local cover boss around one magnet."""

    cx, cy = mount.centre
    width, length = mount.size
    x0 = max(
        metrics.cover.x0,
        metrics.bay_cavity.x0,
        cx - width / 2.0 - mounting.BOSS_X_MARGIN,
    )
    x1 = min(
        metrics.cover.x1,
        metrics.bay_cavity.x1,
        cx + width / 2.0 + mounting.BOSS_X_MARGIN,
    )
    y0 = max(
        metrics.cover.y0,
        metrics.bay_cavity.y0,
        cy - length / 2.0 - mounting.BOSS_Y_MARGIN,
    )
    y1 = min(
        metrics.cover.y1,
        metrics.bay_cavity.y1,
        cy + length / 2.0 + mounting.BOSS_Y_MARGIN,
    )
    return x0, x1, y0, y1


def build_cover(
    keys,
    params=None,
    name="cover",
    compartment_height=None,
    wall=None,
    compartment_depth=None,
    options=None,
):
    """Build the cover with an L profile, local magnets bosses and pockets."""

    options = options or mounting.MountingOptions()
    metrics = case.case_metrics(
        keys,
        params,
        compartment_height=compartment_height,
        wall=wall,
        compartment_depth=compartment_depth,
        options=options,
    )
    lid_thickness = metrics.lid_thickness
    x0, x1 = metrics.cover.x0, metrics.cover.x1
    y0, y1 = metrics.cover.y0, metrics.cover.y1
    z_bottom = case.PLATE_TOP
    z_top = metrics.height
    lip_height = z_top - lid_thickness - z_bottom

    if lip_height <= 1e-9:
        # Flush bay: no vertical wall below the lid, so use a simple box.
        profile = (
            (y0, z_bottom),
            (y0, z_top),
            (y1, z_top),
            (y1, z_bottom),
        )
    else:
        profile = (
            (y0, z_bottom),
            (y0, z_top),
            (y1, z_top),
            (y1, z_top - lid_thickness),
            (y0 + case.COVER_WALL, z_top - lid_thickness),
            (y0 + case.COVER_WALL, z_bottom),
        )

    bm = bmesh.new()
    mesh.add_prism_x(bm, profile, x0, x1)
    cover = mesh.mesh_object(name, bm)

    boss_bm = bmesh.new()
    boss_count = 0
    for mount in metrics.magnets.mounts:
        boss_height = metrics.shelf - mount.rail_top
        if boss_height <= 1e-9:
            continue
        bx0, bx1, by0, by1 = _boss_rect(metrics, mount)
        mesh.add_box(
            boss_bm,
            (
                (bx0 + bx1) / 2.0,
                (by0 + by1) / 2.0,
                (mount.rail_top + metrics.shelf + mounting.BOSS_LID_OVERLAP)
                / 2.0,
            ),
            (
                bx1 - bx0,
                by1 - by0,
                metrics.shelf + mounting.BOSS_LID_OVERLAP - mount.rail_top,
            ),
        )
        boss_count += 1
    if boss_count:
        bosses = mesh.mesh_object(f"{name}_magnet_bosses", boss_bm)
        mesh.union(cover, bosses)
    else:
        boss_bm.free()

    pockets = bmesh.new()
    for mount in metrics.magnets.mounts:
        cx, cy = mount.centre
        width, length = mount.size
        z_min = mount.rail_top - hardware.EPS
        z_max = mount.rail_top + mount.pocket_depth
        mesh.add_box(
            pockets,
            (cx, cy, (z_min + z_max) / 2.0),
            (width, length, z_max - z_min),
        )
    pocket_cutter = mesh.mesh_object(f"{name}_magnet_pockets", pockets)
    mesh.difference(cover, pocket_cutter)

    mesh.check_mesh(name, cover, strict=True)
    return cover
