"""Pure-Python tests for the mounting plans.

Run from the repository root with::

    python3 -m unittest discover -s tests -p "test_*.py" -v
"""

import os
import sys
import unittest

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "keyboards"))

import layout
import mounting


def zero_margin_params():
    return layout.PlateParams(
        margin_left=0.0,
        margin_right=0.0,
        margin_top=0.0,
        margin_bottom=0.0,
    )


class TestMagnetPlan(unittest.TestCase):
    def setUp(self):
        self.opening = layout.Rect(0.0, 80.0, 49.875, 67.375)
        self.cavity = layout.Rect(1.5, 78.5, 49.875, 65.875)
        self.north_start = 20.6
        self.options = mounting.MountingOptions()
        self.plan = mounting.magnet_plan(
            self.opening,
            self.cavity,
            shelf=25.0,
            options=self.options,
            lip_thickness=2.5,
            north_start=self.north_start,
        )

    def test_side_edges_have_two_magnets_each(self):
        side = [m for m in self.plan.mounts if m.side in ("west", "east")]
        self.assertEqual(len(side), 4)
        rail_y0 = self.opening.y0 + 2.5 + 0.2
        for name in ("west", "east"):
            centres = sorted(
                m.centre[1] for m in side if m.side == name
            )
            self.assertEqual(len(centres), 2)
            self.assertAlmostEqual(centres[0], rail_y0 + 3.2)
            self.assertAlmostEqual(centres[1], self.cavity.y1 - 3.2)
        self.assertEqual(self.plan.top_count, 2)
        top = [m for m in self.plan.mounts if m.side == "north"]
        top_x0 = self.north_start + mounting.TOP_EDGE_MARGIN
        side_width = side[0].shelf_width
        top_x1 = self.cavity.x1 - side_width - mounting.TOP_EDGE_MARGIN
        step_x = (top_x1 - top_x0) / 3.0
        centres = sorted(m.centre[0] for m in top)
        self.assertAlmostEqual(centres[0], top_x0 + step_x)
        self.assertAlmostEqual(centres[1], top_x0 + 2.0 * step_x)
        self.assertEqual(len(self.plan.mounts), 6)
        self.assertEqual(len({mount.rail for mount in self.plan.mounts}), 3)
        side_mount = side[0]
        self.assertAlmostEqual(side_mount.size[0], 3.4)
        self.assertAlmostEqual(side_mount.size[1], 6.4)
        self.assertAlmostEqual(side_mount.shelf_width, 6.4)
        self.assertAlmostEqual(side_mount.shelf_length, 6.4)
        self.assertAlmostEqual(side_mount.pocket_depth, 3.1)
        self.assertAlmostEqual(side_mount.rail_top, 23.5)
        self.assertAlmostEqual(25.0 - side_mount.rail_top, 1.5)
        top_mount = top[0]
        self.assertAlmostEqual(top_mount.size[0], 6.4)
        self.assertAlmostEqual(top_mount.size[1], 3.4)
        self.assertAlmostEqual(top_mount.shelf_width, 6.4)
        self.assertAlmostEqual(top_mount.shelf_length, 7.2)
        self.assertAlmostEqual(top_mount.pocket_depth, 3.1)
        self.assertAlmostEqual(top_mount.rail_top, 23.5)
        self.assertAlmostEqual(25.0 - top_mount.rail_top, 1.5)

    def test_magnets_sit_inside_their_rails(self):
        for mount in self.plan.mounts:
            x0, x1, y0, y1 = mount.rail
            cx, cy = mount.centre
            width, length = mount.size
            self.assertGreaterEqual(cx - width / 2.0, x0 - 1e-9)
            self.assertLessEqual(cx + width / 2.0, x1 + 1e-9)
            self.assertGreaterEqual(cy - length / 2.0, y0 - 1e-9)
            self.assertLessEqual(cy + length / 2.0, y1 + 1e-9)

    def test_short_side_span_uses_one_magnet_per_side(self):
        options = mounting.MountingOptions(magnet_width=10.0)
        plan = mounting.magnet_plan(
            self.opening,
            self.cavity,
            shelf=25.0,
            options=options,
            lip_thickness=2.5,
            north_start=self.north_start,
        )
        side = [m for m in plan.mounts if m.side in ("west", "east")]
        self.assertEqual(len(side), 2)
        mid = (self.opening.y0 + 2.5 + 0.2 + self.cavity.y1) / 2.0
        for mount in side:
            self.assertAlmostEqual(mount.centre[1], mid)

    def test_side_span_too_short_for_even_one_magnet_rejected(self):
        options = mounting.MountingOptions(magnet_width=15.0)
        with self.assertRaises(ValueError):
            mounting.magnet_plan(
                self.opening,
                self.cavity,
                shelf=25.0,
                options=options,
                lip_thickness=2.5,
                north_start=self.north_start,
            )

    def test_top_count_adapts_to_spacing(self):
        options = mounting.MountingOptions(magnet_spacing=20.0)
        plan = mounting.magnet_plan(
            self.opening,
            self.cavity,
            shelf=25.0,
            options=options,
            lip_thickness=2.5,
            north_start=self.north_start,
        )
        self.assertEqual(plan.top_count, 3)
        self.assertEqual(len(plan.mounts), 7)

    def test_bay_too_narrow_rejected(self):
        with self.assertRaises(ValueError):
            mounting.magnet_plan(
                layout.Rect(0.0, 12.0, 49.875, 67.375),
                layout.Rect(1.5, 10.5, 49.875, 65.875),
                shelf=25.0,
                options=self.options,
                lip_thickness=2.5,
                north_start=self.north_start,
            )

    def test_bay_too_shallow_rejected(self):
        with self.assertRaises(ValueError):
            mounting.magnet_plan(
                layout.Rect(0.0, 80.0, 49.875, 57.375),
                layout.Rect(1.5, 78.5, 49.875, 55.875),
                shelf=25.0,
                options=self.options,
                lip_thickness=2.5,
                north_start=self.north_start,
            )

    def test_boss_height_follows_magnet_thickness(self):
        self.assertAlmostEqual(
            mounting.MountingOptions(magnet_thickness=2.0).magnet_boss_height(
                2.0
            ),
            0.5,
        )
        self.assertAlmostEqual(
            mounting.MountingOptions(magnet_thickness=1.0).magnet_boss_height(
                1.0
            ),
            0.0,
        )
        self.assertAlmostEqual(
            mounting.MountingOptions(magnet_thickness=4.0).magnet_boss_height(
                4.0
            ),
            2.5,
        )


class TestMountingOptions(unittest.TestCase):
    def test_defaults_validate(self):
        options = mounting.MountingOptions()
        self.assertIs(options.validate(), options)

    def test_invalid_dimensions_rejected(self):
        with self.assertRaises(ValueError):
            mounting.MountingOptions(magnet_width=-1.0).validate()
        with self.assertRaises(ValueError):
            mounting.MountingOptions(wire_gap=2.3).validate()
        with self.assertRaises(ValueError):
            mounting.MountingOptions(magnet_spacing=0.0).validate()


class TestRibPlan(unittest.TestCase):
    def test_one_rib_between_two_1u_keys(self):
        keys = [layout.Key(0.0, 0.0), layout.Key(layout.PITCH, 0.0)]
        bounds = layout.layout_bounds(keys, zero_margin_params())
        ribs = mounting.rib_plan(keys, bounds)
        self.assertEqual(len(ribs), 1)
        self.assertAlmostEqual(ribs[0].centre_x, 0.0)
        self.assertAlmostEqual(ribs[0].centre_y, 0.0)
        self.assertAlmostEqual(ribs[0].width, mounting.RIB_WIDTH)
        self.assertAlmostEqual(ribs[0].height, 10.0)

    def test_gap_creates_no_rib(self):
        keys = [layout.Key(0.0, 0.0), layout.Key(2.0 * layout.PITCH, 0.0)]
        bounds = layout.layout_bounds(keys, zero_margin_params())
        self.assertEqual(mounting.rib_plan(keys, bounds), ())

    def test_wide_key_boundary_is_supported(self):
        keys = [
            layout.Key(0.0, 0.0, width=2.0 * layout.PITCH),
            layout.Key(1.5 * layout.PITCH, 0.0),
        ]
        bounds = layout.layout_bounds(keys, zero_margin_params())
        ribs = mounting.rib_plan(keys, bounds)
        self.assertEqual(len(ribs), 1)
        self.assertAlmostEqual(ribs[0].centre_x, 0.5 * layout.PITCH)

    def test_no_rib_without_y_overlap(self):
        keys = [
            layout.Key(0.0, 0.0),
            layout.Key(layout.PITCH, 2.0 * layout.PITCH),
        ]
        bounds = layout.layout_bounds(keys, zero_margin_params())
        self.assertEqual(mounting.rib_plan(keys, bounds), ())


if __name__ == "__main__":
    unittest.main()
