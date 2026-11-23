"""Blender-side geometry tests.

Run from the repository root with::

    blender --background --python-exit-code 1 --python tests/run_blender_tests.py

The pure layout/mounting tests live in ``test_*.py`` and run under plain
Python.  This file checks the generated metrics for all four boards, builds the
numpad plate, case and cover (including the split plate), and splits the 60%
edge ring into its four dovetailed pieces.
"""

import importlib.util
import os
import sys

import bpy

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "keyboards"))

import case
import case_split
import cli
import cover
import export
import generate
import hardware
import layout
import mounting
import plate

NAMES = ("kbNumpad", "kb60", "kb75", "kb100")


def close(actual, expected, tolerance=1e-6):
    assert abs(actual - expected) <= tolerance, (actual, expected)


def cast_z(obj, x, y, z_start, direction):
    hit, location, _normal, _face = obj.ray_cast(
        (x, y, z_start), (0.0, 0.0, direction)
    )
    return location.z if hit else None


def load_keys(name):
    path = os.path.join(ROOT, "keyboards", name, "main.py")
    spec = importlib.util.spec_from_file_location(f"test_{name}", path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module.KEYS


def check_metrics(name, keys):
    options = mounting.MountingOptions()
    metrics = case.case_metrics(keys, options=options)
    close(metrics.lid_thickness, 2.5)
    close(metrics.shelf, 27.5)
    close(metrics.magnets.mounts[0].rail_top, 26.0)
    assert len(metrics.plate_screws) == 2, f"{name}: plate screws"
    assert len(metrics.edge_screws) == 4, f"{name}: edge screws"
    assert metrics.plate_screws[0][0] < 0.0 < metrics.plate_screws[1][0], name
    edge_xs = [position[0] for position in metrics.edge_screws]
    assert edge_xs == sorted(edge_xs), f"{name}: unsorted edge screws"
    assert len(set(round(x, 4) for x in edge_xs)) == 4, name
    side = [
        mount for mount in metrics.magnets.mounts
        if mount.side in ("west", "east")
    ]
    top = [
        mount for mount in metrics.magnets.mounts if mount.side == "north"
    ]
    assert len(side) == 4, f"{name}: side edges need four magnets"
    assert metrics.magnets.top_count >= 1, f"{name}: no top-edge magnets"
    assert len(top) == metrics.magnets.top_count, name
    assert metrics.ribs, f"{name}: no support ribs"
    assert metrics.tabs.count >= 3, f"{name}: no plate locating tabs"
    check_tab_fit(name, metrics)
    close(side[0].size[0], 3.4)
    close(side[0].size[1], 6.4)
    close(top[0].size[0], 6.4)
    close(top[0].size[1], 3.4)
    for mount in metrics.magnets.mounts:
        assert (
            mount.rail_top - mount.pocket_depth > case.FLOOR + 1.0
        ), f"{name}: magnet pocket too close to the case floor"

    dense_options = mounting.MountingOptions(magnet_spacing=20.0)
    dense_metrics = case.case_metrics(keys, options=dense_options)
    assert dense_metrics.magnets.top_count >= metrics.magnets.top_count, name
    print(
        f"{name}: {len(metrics.magnets.mounts)} magnets "
        f"(4 side + {metrics.magnets.top_count} top), "
        f"{len(metrics.ribs)} ribs, {metrics.tabs.count} plate tabs"
    )


def check_tab_fit(name, metrics):
    pocket = metrics.plate_pocket
    bottom = metrics.bottom
    edge = metrics.edge_lower

    for tab in metrics.tabs.tabs:
        if tab.side == "west":
            tab_outer = tab.centre[0] - tab.depth / 2.0
            tab_inner = tab.centre[0] + tab.depth / 2.0
            tray_outer = bottom.x0
            lip_inner = bottom.x0 + case.BOTTOM_LIP_WIDTH
            edge_gap = bottom.x0 - edge.x0
        elif tab.side == "east":
            tab_outer = tab.centre[0] + tab.depth / 2.0
            tab_inner = tab.centre[0] - tab.depth / 2.0
            tray_outer = bottom.x1
            lip_inner = bottom.x1 - case.BOTTOM_LIP_WIDTH
            edge_gap = edge.x1 - bottom.x1
        else:
            tab_outer = tab.centre[1] - tab.depth / 2.0
            tab_inner = tab.centre[1] + tab.depth / 2.0
            tray_outer = bottom.y0
            lip_inner = bottom.y0 + case.BOTTOM_LIP_WIDTH
            edge_gap = bottom.y0 - edge.y0

        # The tab reaches the tray's outer face, which is 0.2 mm inside the
        # edge-ring lower wall; it passes through the lip cutout on the way.
        assert abs(tab_outer - tray_outer) <= 0.05, (
            f"{name}: tab outer face {tab_outer:.3f} does not reach the tray "
            f"outer face {tray_outer:.3f}"
        )
        # The lip stops short of the plate edge but close enough to cradle it.
        lip_gap = (pocket.x0 + case.PLATE_CLEARANCE) - lip_inner
        if tab.side == "east":
            lip_gap = lip_inner - (pocket.x1 - case.PLATE_CLEARANCE)
        elif tab.side == "south":
            lip_gap = (pocket.y0 + case.PLATE_CLEARANCE) - lip_inner
        assert 0.0 < lip_gap <= 1.0, f"{name}: lip/plate gap {lip_gap:.3f}"
        assert edge_gap >= 0.1, f"{name}: lower edge clearance {edge_gap:.3f}"

    close(metrics.tab_top, case.TAB_TOP)
    close(metrics.tab_top, case.FLOOR + mounting.TAB_HEIGHT)


def check_numpad_build(keys):
    export.reset_scene()
    plate_obj = plate.build_top_plate(keys, name="test_plate")
    case_bottom = case.build_case_bottom(keys, name="test_case_bottom")
    case_edges = case.build_case_edges(keys, name="test_case_edges")
    cover_obj = cover.build_cover(keys, name="test_cover")
    bpy.context.view_layer.update()

    # The plate's bounding box includes the lower tabs, which now reach the
    # bottom tray's outer face (0.2 mm inside the edge-ring lower wall).
    close(plate_obj.dimensions.x, 89.2, 0.05)
    close(plate_obj.dimensions.y, 103.75, 0.05)
    close(plate_obj.dimensions.z, 9.5, 0.05)
    close(case_bottom.dimensions.x, 89.2, 0.05)
    close(case_bottom.dimensions.y, 135.75, 0.05)
    close(case_bottom.dimensions.z, 7.8, 0.05)
    close(case_edges.dimensions.x, 94.7, 0.05)
    close(case_edges.dimensions.y, 141.25, 0.05)
    close(case_edges.dimensions.z, 30.0, 0.05)
    close(cover_obj.dimensions.x, 80.3, 0.05)
    close(cover_obj.dimensions.y, 27.3, 0.05)
    close(cover_obj.dimensions.z, 16.5, 0.05)

    bottom_min_z = min(
        (case_bottom.matrix_world @ vertex.co).z
        for vertex in case_bottom.data.vertices
    )
    edges_min_z = min(
        (case_edges.matrix_world @ vertex.co).z
        for vertex in case_edges.data.vertices
    )
    close(bottom_min_z, 0.0, 1e-6)
    close(edges_min_z, 0.0, 1e-6)

    top, bottom = plate.split_top_plate(plate_obj, name="test_split")
    bpy.context.view_layer.update()
    close(top.dimensions.z, 5.75, 0.05)
    close(bottom.dimensions.z, 3.75, 0.05)
    # Both halves reach the full tab footprint: the top half carries the 0.25 mm
    # top edge of the tabs and the bottom half the lower 3.75 mm.
    close(top.dimensions.x, plate_obj.dimensions.x, 0.05)
    close(top.dimensions.y, plate_obj.dimensions.y, 0.05)
    close(bottom.dimensions.x, plate_obj.dimensions.x, 0.05)
    close(bottom.dimensions.y, plate_obj.dimensions.y, 0.05)

    metrics = case.case_metrics(keys)
    for x, y in metrics.plate_screws:
        close(cast_z(plate_obj, x, y, 20.0, -1.0), 9.5, 0.05)
        close(cast_z(plate_obj, x, y, -1.0, 1.0), hardware.PLATE_SCREW_PILOT_DEPTH, 0.05)
    for x, y in metrics.edge_screws:
        assert cast_z(case_bottom, x, y, -1.0, 1.0) is None
        close(
            cast_z(case_edges, x, y, -1.0, 1.0),
            case.FLOOR + case.EDGE_BOX_SCREW_DEPTH,
            0.05,
        )
    wedge_x = 10.0
    wedge_y = metrics.edge_lower.y0 + 1.5
    close(
        cast_z(case_edges, wedge_x, wedge_y, -1.0, 1.0),
        case.FRONT_WEDGE_BOTTOM,
        0.05,
    )

    print("kbNumpad: plate, split case, cover and split plate built")


def check_case_split(keys, name="kb60"):
    """A 60% edge ring must split into four validated dovetail pieces."""

    export.reset_scene()
    metrics = case.case_metrics(keys)
    assert case_split.should_split(metrics), name
    west_cut, east_cut = case_split.split_positions(metrics.width)
    close(west_cut, -metrics.width / 4.0)
    close(east_cut, metrics.width / 4.0)

    edges = case.build_case_edges(keys, name=f"test_{name}_edges")
    pieces = case_split.split_case_edges(
        edges, metrics, name=f"test_{name}_edges"
    )
    assert [suffix for suffix, _ in pieces] == list(case_split.PIECE_SUFFIXES)

    extents = {}
    for suffix, piece in pieces:
        xs = [vertex.co.x for vertex in piece.data.vertices]
        zs = [vertex.co.z for vertex in piece.data.vertices]
        extents[suffix] = max(xs) - min(xs)
        assert extents[suffix] <= case_split.SPLIT_CASE_MAX_WIDTH + 1e-6, (
            suffix,
            extents[suffix],
        )
        close(min(zs), 0.0, 1e-6)
        close(max(zs), metrics.height if suffix != "south" else case.CASE_HEIGHT, 0.05)

    # The middle rails carry the male plugs, so they reach one plug depth past
    # each seam.  The side pieces stop at the seam faces.
    middle = metrics.width / 2.0 + 2.0 * case_split.PLUG_DEPTH
    side = metrics.width / 4.0 - case_split.SEAM_GAP / 2.0
    close(extents["north"], middle, 0.05)
    close(extents["south"], middle, 0.05)
    close(extents["west"], side, 0.05)
    close(extents["east"], side, 0.05)

    print(
        f"{name}: edge ring split into {len(pieces)} pieces, "
        f"widest {max(extents.values()):.1f} mm"
    )


def check_output_paths():
    """The default tree is build/<name>/{top_plate,case,cover}/... ."""

    plate_path, bottom_path, edges_path, cover_path = cli.default_paths(
        ROOT, "kb60"
    )
    base = os.path.join(ROOT, "build", "kb60")
    assert plate_path == os.path.join(base, "top_plate", "top_plate.stl")
    assert bottom_path == os.path.join(base, "case", "case_bottom.stl")
    assert edges_path == os.path.join(base, "case", "case_edges.stl")
    assert cover_path == os.path.join(base, "cover", "cover.stl")

    west_path = case_split.piece_path(edges_path, "west")
    assert west_path == os.path.join(base, "case", "case_edges_west.stl")
    top_half, bottom_half = generate._plate_split_paths(plate_path)
    assert top_half == os.path.join(base, "top_plate", "top_plate_top_half.stl")
    assert bottom_half == os.path.join(
        base, "top_plate", "top_plate_bottom_half.stl"
    )

    print("default output tree: build/<name>/{top_plate,case,cover}/")


def main():
    layouts = {}
    for name in NAMES:
        keys = load_keys(name)
        layouts[name] = keys
        check_metrics(name, keys)

    check_numpad_build(layouts["kbNumpad"])
    assert case_split.should_split(case.case_metrics(layouts["kbNumpad"]))
    check_case_split(layouts["kb60"])
    check_output_paths()

    # The south-facing 100% plate used to leave a tiny boolean sliver at a
    # hotswap pocket corner; build it every run so the repair stays covered.
    south = plate.build_top_plate(
        layouts["kb100"],
        name="test_kb100_south_plate",
        params=layout.PlateParams(led_facing=layout.LED_FACING_SOUTH),
    )
    close(south.dimensions.z, 9.5, 0.05)
    print("kb100: south-facing plate built")

    print("Blender geometry tests OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
