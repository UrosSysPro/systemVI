"""Blender-side geometry tests.

Run from the repository root with::

    blender --background --python-exit-code 1 --python tests/run_blender_tests.py

The pure layout/mounting tests live in ``test_*.py`` and run under plain
Python.  This file checks the generated metrics for all four boards and builds
the numpad plate, case and cover (including the split plate) through the real
builders.
"""

import importlib.util
import os
import sys

import bpy

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "keyboards"))

import case
import cover
import export
import mounting
import plate

NAMES = ("kbNumpad", "kb60", "kb75", "kb100")


def close(actual, expected, tolerance=1e-6):
    assert abs(actual - expected) <= tolerance, (actual, expected)


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
    close(metrics.shelf, 25.0)
    close(metrics.magnets.mounts[0].rail_top, 23.5)
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
        f"{len(metrics.ribs)} ribs"
    )


def check_numpad_build(keys):
    export.reset_scene()
    plate_obj = plate.build_top_plate(keys, name="test_plate")
    case_obj = case.build_case(keys, name="test_case")
    cover_obj = cover.build_cover(keys, name="test_cover")
    bpy.context.view_layer.update()

    close(plate_obj.dimensions.x, 80.2, 0.05)
    close(plate_obj.dimensions.y, 99.25, 0.05)
    close(plate_obj.dimensions.z, 9.5, 0.05)
    close(case_obj.dimensions.x, 94.7, 0.05)
    close(case_obj.dimensions.y, 131.25, 0.05)
    close(case_obj.dimensions.z, 27.5, 0.05)
    close(cover_obj.dimensions.x, 80.3, 0.05)
    close(cover_obj.dimensions.y, 17.3, 0.05)
    close(cover_obj.dimensions.z, 15.0, 0.05)

    top, bottom = plate.split_top_plate(plate_obj, name="test_split")
    bpy.context.view_layer.update()
    close(top.dimensions.z, 5.75, 0.05)
    close(bottom.dimensions.z, 3.75, 0.05)
    close(top.dimensions.x, plate_obj.dimensions.x, 0.05)
    close(bottom.dimensions.y, plate_obj.dimensions.y, 0.05)

    print("kbNumpad: plate, case, cover and split plate built")


def main():
    layouts = {}
    for name in NAMES:
        keys = load_keys(name)
        layouts[name] = keys
        check_metrics(name, keys)

    check_numpad_build(layouts["kbNumpad"])
    print("Blender geometry tests OK")
    return 0


if __name__ == "__main__":
    sys.exit(main())
