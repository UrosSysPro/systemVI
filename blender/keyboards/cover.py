"""L-shaped cover for the north electronics bay.

The horizontal leg rests on the bay shelf.  When the bay is raised, the
vertical leg hangs down in front of the top row of keys to the plate surface.
When the bay is flush, there is no lip below the lid, so the cover is a plain
box; the builder special-cases that instead of emitting a degenerate profile.
"""

from __future__ import annotations

import bmesh

import case
import mesh


def build_cover(
    keys,
    params=None,
    name="cover",
    compartment_height=None,
    wall=None,
    compartment_depth=None,
):
    """Build the cover with an L-shaped side profile.

    The horizontal leg is the top plate that rests on the bay shelf; the
    vertical leg is the wall hanging down in front of the top row of keys,
    reaching the top plate surface.  When the compartment is raised the case
    carries the slanted edges, so the cover itself stays a clean L.
    """

    metrics = case.case_metrics(
        keys,
        params,
        compartment_height=compartment_height,
        wall=wall,
        compartment_depth=compartment_depth,
    )
    x0, x1 = metrics.cover.x0, metrics.cover.x1
    y0, y1 = metrics.cover.y0, metrics.cover.y1
    z_bottom = case.PLATE_TOP
    z_top = metrics.height
    lip_height = z_top - case.LID_THICKNESS - z_bottom

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
            (y1, z_top - case.LID_THICKNESS),
            (y0 + case.COVER_WALL, z_top - case.LID_THICKNESS),
            (y0 + case.COVER_WALL, z_bottom),
        )

    bm = bmesh.new()
    mesh.add_prism_x(bm, profile, x0, x1)
    cover = mesh.mesh_object(name, bm)

    mesh.check_mesh(name, cover, strict=True)
    return cover
