"""Pure-Python tests for the layout model.

Run from the repository root with::

    python3 -m unittest discover -s tests -p "test_*.py" -v
"""

import math
import os
import sys
import unittest

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(ROOT, "keyboards"))

import layout


class TestRect(unittest.TestCase):
    def test_properties(self):
        rect = layout.Rect(-1.0, 3.0, -2.0, 4.0)
        self.assertAlmostEqual(rect.width, 4.0)
        self.assertAlmostEqual(rect.height, 6.0)
        self.assertAlmostEqual(rect.centre_x, 1.0)
        self.assertAlmostEqual(rect.centre_y, 1.0)


class TestPlateParams(unittest.TestCase):
    def test_defaults(self):
        params = layout.PlateParams()
        self.assertAlmostEqual(params.margin_left, layout.PLATE_MARGIN)
        self.assertAlmostEqual(params.margin_right, layout.PLATE_MARGIN)
        self.assertEqual(params.led_facing, layout.LED_FACING_NORTH)

    def test_negative_margin_rejected(self):
        with self.assertRaises(ValueError):
            layout.PlateParams(margin_left=-0.1)

    def test_facing_angles(self):
        north = layout.PlateParams(led_facing=layout.LED_FACING_NORTH)
        south = layout.PlateParams(led_facing=layout.LED_FACING_SOUTH)
        self.assertAlmostEqual(north.facing_angle(), 0.0)
        self.assertAlmostEqual(south.facing_angle(), math.pi)

    def test_invalid_facing_rejected(self):
        params = layout.PlateParams(led_facing="east")
        with self.assertRaises(ValueError):
            params.facing_angle()


class TestKey(unittest.TestCase):
    def test_unrotated_bounds(self):
        key = layout.Key(10.0, 20.0, width=38.1, height=19.05)
        bounds = key.bounds()
        self.assertAlmostEqual(bounds.x0, -9.05)
        self.assertAlmostEqual(bounds.x1, 29.05)
        self.assertAlmostEqual(bounds.y0, 10.475)
        self.assertAlmostEqual(bounds.y1, 29.525)

    def test_rotated_bounds_swap_dimensions(self):
        key = layout.Key(
            0.0, 0.0, width=38.1, height=19.05, rotation=math.pi / 2.0
        )
        bounds = key.bounds()
        self.assertAlmostEqual(bounds.width, 19.05)
        self.assertAlmostEqual(bounds.height, 38.1)
        self.assertAlmostEqual(bounds.x0, -9.525)
        self.assertAlmostEqual(bounds.y0, -19.05)


class TestKeysFromRows(unittest.TestCase):
    def test_positions_and_gap(self):
        keys = layout.keys_from_rows([[1.0, 2.0], [None, 1.0]])
        self.assertEqual(len(keys), 3)
        self.assertAlmostEqual(keys[0].x, 0.5 * layout.PITCH)
        self.assertAlmostEqual(keys[0].width, layout.PITCH)
        self.assertAlmostEqual(keys[1].x, 2.0 * layout.PITCH)
        self.assertAlmostEqual(keys[1].width, 2.0 * layout.PITCH)
        self.assertAlmostEqual(keys[1].y, 0.0)
        self.assertAlmostEqual(keys[2].x, 1.5 * layout.PITCH)
        self.assertAlmostEqual(keys[2].y, -layout.PITCH)


class TestKeysFromUnits(unittest.TestCase):
    def test_width_height_and_rotation(self):
        key = layout.keys_from_units([(1.0, 2.0, 1.5, 1.0, 90.0)])[0]
        self.assertAlmostEqual(key.x, layout.PITCH)
        self.assertAlmostEqual(key.y, 2.0 * layout.PITCH)
        self.assertAlmostEqual(key.width, 1.5 * layout.PITCH)
        self.assertAlmostEqual(key.height, layout.PITCH)
        self.assertAlmostEqual(key.rotation, math.pi / 2.0)

    def test_defaults(self):
        key = layout.keys_from_units([(0.0, 0.0)])[0]
        self.assertAlmostEqual(key.width, layout.PITCH)
        self.assertAlmostEqual(key.height, layout.PITCH)
        self.assertAlmostEqual(key.rotation, 0.0)


class TestLayoutBounds(unittest.TestCase):
    def test_margins_are_applied(self):
        keys = [layout.Key(0.0, 0.0), layout.Key(layout.PITCH, 0.0)]
        params = layout.PlateParams(
            margin_left=1.0,
            margin_right=2.0,
            margin_top=3.0,
            margin_bottom=4.0,
        )
        bounds = layout.layout_bounds(keys, params)
        half = layout.PITCH / 2.0
        self.assertAlmostEqual(bounds.x0, -half - 1.0)
        self.assertAlmostEqual(bounds.x1, layout.PITCH + half + 2.0)
        self.assertAlmostEqual(bounds.y0, -half - 4.0)
        self.assertAlmostEqual(bounds.y1, half + 3.0)

    def test_empty_layout_rejected(self):
        with self.assertRaises(ValueError):
            layout.layout_bounds([])


if __name__ == "__main__":
    unittest.main()
