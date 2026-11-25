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


class TestTiltPlan(unittest.TestCase):
    def setUp(self):
        self.bay = layout.Rect(-40.0, 40.0, 50.0, 77.5)
        self.plan = mounting.tilt_plan(100.0, self.bay)

    def test_bar_dimensions(self):
        self.assertAlmostEqual(self.plan.width, 80.0)
        self.assertAlmostEqual(self.plan.depth, mounting.TILT_DEPTH)
        self.assertAlmostEqual(self.plan.height, mounting.TILT_HEIGHT)
        self.assertAlmostEqual(self.plan.bevel, mounting.TILT_BEVEL)
        self.assertAlmostEqual(
            self.plan.mount_y - self.plan.depth / 2.0,
            self.bay.y0 + mounting.TILT_BAY_CLEARANCE,
        )

    def test_six_magnets_in_one_row(self):
        self.assertEqual(self.plan.magnet_count, 6)
        self.assertAlmostEqual(
            self.plan.bar_magnet_pocket_depth,
            mounting.TILT_MAGNET_THICKNESS + 0.1,
        )
        self.assertAlmostEqual(
            self.plan.case_magnet_pocket_depth,
            mounting.TILT_MAGNET_THICKNESS,
        )
        self.assertAlmostEqual(
            self.plan.case_magnet_bottom_skin,
            mounting.TILT_CASE_MAGNET_BOTTOM_SKIN,
        )
        self.assertAlmostEqual(
            self.plan.cover_thickness, mounting.TILT_MAGNET_COVER
        )
        self.assertAlmostEqual(
            self.plan.case_magnet_bottom_skin
            + self.plan.case_magnet_pocket_depth
            + self.plan.cover_thickness,
            4.0,
        )
        ys = {magnet.centre[1] for magnet in self.plan.magnets}
        self.assertEqual(len(ys), 1)
        self.assertAlmostEqual(next(iter(ys)), mounting.TILT_MAGNET_Y)
        for magnet in self.plan.magnets:
            self.assertAlmostEqual(
                magnet.size[0], mounting.TILT_MAGNET_WIDTH + 0.4
            )
            self.assertAlmostEqual(
                magnet.size[1], mounting.TILT_MAGNET_LENGTH + 0.4
            )
        centres = sorted(magnet.centre[0] for magnet in self.plan.magnets)
        self.assertLess(centres[0], 0.0)
        self.assertGreater(centres[-1], 0.0)
        # Six is even, so no magnet pocket straddles the x = 0 print split.
        for magnet in self.plan.magnets:
            self.assertGreaterEqual(
                abs(magnet.centre[0]) - magnet.size[0] / 2.0,
                mounting.TILT_SEAM_CLEARANCE,
            )

    def test_magnets_are_behind_the_pins(self):
        magnet_y = next(
            magnet.centre[1] for magnet in self.plan.magnets
        )
        for pin in self.plan.pins:
            self.assertGreater(magnet_y, pin.centre[1])

    def test_two_wide_locating_pins(self):
        self.assertEqual(self.plan.pin_count, 2)
        for pin in self.plan.pins:
            self.assertAlmostEqual(
                abs(pin.centre[0]),
                mounting.TILT_PIN_X_FRACTION * self.plan.width,
            )
            self.assertAlmostEqual(pin.centre[1], mounting.TILT_PIN_Y)
            self.assertAlmostEqual(
                pin.width, mounting.TILT_PIN_WIDTH_FRACTION * self.plan.width
            )
            self.assertAlmostEqual(pin.depth, mounting.TILT_PIN_DEPTH)
            self.assertAlmostEqual(pin.height, mounting.TILT_PIN_HEIGHT)
            self.assertLess(
                abs(pin.centre[0]) + pin.width / 2.0,
                self.plan.width / 2.0,
            )

    def test_magnets_and_pins_keep_their_distance(self):
        magnet_front = min(
            magnet.centre[1] - magnet.size[1] / 2.0
            for magnet in self.plan.magnets
        )
        pin_rear = max(
            pin.centre[1] + pin.depth / 2.0 for pin in self.plan.pins
        )
        self.assertGreaterEqual(
            magnet_front - pin_rear, mounting.TILT_MIN_WALL
        )

    def test_shallow_bay_rejected(self):
        with self.assertRaises(ValueError):
            mounting.tilt_plan(
                100.0, layout.Rect(-40.0, 40.0, 50.0, 60.0)
            )

    def test_narrow_case_rejected(self):
        with self.assertRaises(ValueError):
            mounting.tilt_plan(20.0, self.bay)


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


class TestPlateTabPlan(unittest.TestCase):
    def test_adaptive_rows_and_tab_positions(self):
        width, height = 80.2, 99.25
        plan = mounting.plate_tab_plan(width, height)
        self.assertEqual(len(plan.by_side("west")), 2)
        self.assertEqual(len(plan.by_side("east")), 2)
        self.assertEqual(len(plan.by_side("south")), 1)
        self.assertEqual(plan.count, 5)

        outward = width / 2.0 + mounting.TAB_DEPTH / 2.0
        for tab in plan.tabs:
            self.assertAlmostEqual(tab.width, mounting.TAB_WIDTH)
            self.assertAlmostEqual(tab.depth, mounting.TAB_DEPTH)
            if tab.side == "west":
                self.assertAlmostEqual(tab.centre[0], -outward)
            elif tab.side == "east":
                self.assertAlmostEqual(tab.centre[0], outward)
            else:
                self.assertAlmostEqual(tab.centre[0], 0.0)
                self.assertAlmostEqual(
                    tab.centre[1],
                    -(height / 2.0 + mounting.TAB_DEPTH / 2.0),
                )

        # Tabs sit evenly inside each side and stay clear of the corners.
        for side in ("west", "east"):
            centres = [tab.centre[1] for tab in plan.by_side(side)]
            self.assertLess(centres[0], centres[1])
            self.assertGreater(
                centres[0] - mounting.TAB_WIDTH / 2.0, -height / 2.0
            )
            self.assertLess(
                centres[-1] + mounting.TAB_WIDTH / 2.0, height / 2.0
            )

    def test_more_tabs_on_a_long_board(self):
        short = mounting.plate_tab_plan(80.2, 99.25)
        long = mounting.plate_tab_plan(447.0, 119.0)
        self.assertGreater(long.count, short.count)
        self.assertGreater(len(long.by_side("south")), 1)

    def test_side_too_short_rejected(self):
        with self.assertRaises(ValueError):
            mounting.plate_tab_plan(8.0, 8.0)


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
