"""Render an exploded view of a keyboard.

The layers are separated vertically, bottom to top:

- the bottom case tray at z = 0,
- the edge ring in the middle: on a wide case the two U-shaped side pieces
  spread outward along X while the north and south rails stay at their wall
  positions (the numpad's one-piece ring stays together),
- the top plate above them (or its two halves with ``--split-plate``),
- the cover at the very top.

Run headlessly with:

    blender --background --python-exit-code 1 --python keyboards/render_exploded.py -- \
        --keyboard kb75

Options:

- ``--keyboard kbNumpad|kb60|kb75|kb100``
- ``--split-plate`` — show the top plate as its two horizontal halves
- ``--spread MM`` — how far the west/east side pieces move outward along X
  (default 50 mm); the two middle rails stay at their wall positions
- ``--gap MM`` — bottom tray to the edge ring (default 50 mm)
- ``--plate-gap MM`` — edge-ring top to the plate (default 35 mm)
- ``--plate-half-gap MM`` — gap between the two split plate halves
  (default 14 mm)
- ``--cover-gap MM`` — plate top to the cover (default 30 mm)
- ``--azimuth DEG``, ``--elevation DEG`` — camera orbit (default 35/30)
- ``--orthographic``, ``--margin F``
- ``--samples N``, ``--width PX``, ``--height PX``
- ``--output PATH`` — PNG path (default ``build/<keyboard>_exploded.png``)

The script builds the same default parts as the generator and writes only a
PNG; it does not export STLs.
"""

from __future__ import annotations

import argparse
import math
import os
import sys

import bpy
from mathutils import Vector

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, SCRIPT_DIR)

import case as case_module
import case_split
import cli
import export
import plate as plate_module
import render_preview as preview

# Exploded view defaults, in millimetres.
EDGE_SPREAD = 50.0
BOTTOM_GAP = 50.0
PLATE_GAP = 45.0
PLATE_HALF_GAP = 14.0
COVER_GAP = 30.0


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--keyboard",
        choices=sorted(preview.KEYBOARDS),
        default="kb75",
        help="keyboard layout to render (default: %(default)s)",
    )
    parser.add_argument(
        "--output",
        metavar="PATH",
        help="PNG path (default: build/<keyboard>_exploded.png)",
    )
    parser.add_argument(
        "--split-plate",
        action="store_true",
        help="show the top plate as its two horizontal halves",
    )
    parser.add_argument(
        "--spread",
        type=float,
        default=EDGE_SPREAD,
        metavar="MM",
        help="west/east side-piece outward spread along X (default: "
        "%(default)s); the middle rails stay at their wall positions",
    )
    parser.add_argument(
        "--gap",
        type=float,
        default=BOTTOM_GAP,
        metavar="MM",
        help="gap from the bottom tray to the edge ring (default: %(default)s)",
    )
    parser.add_argument(
        "--plate-gap",
        type=float,
        default=PLATE_GAP,
        metavar="MM",
        help="gap from the edge-ring top to the plate (default: %(default)s)",
    )
    parser.add_argument(
        "--plate-half-gap",
        type=float,
        default=PLATE_HALF_GAP,
        metavar="MM",
        help="gap between split plate halves (default: %(default)s)",
    )
    parser.add_argument(
        "--cover-gap",
        type=float,
        default=COVER_GAP,
        metavar="MM",
        help="gap from the plate top to the cover (default: %(default)s)",
    )
    parser.add_argument(
        "--azimuth",
        type=float,
        default=35.0,
        metavar="DEG",
        help="orbit angle: 0 south, 90 east, 180 north, 270 west "
        "(default: %(default)s)",
    )
    parser.add_argument(
        "--elevation",
        type=float,
        default=30.0,
        metavar="DEG",
        help="angle above the XY plane (default: %(default)s)",
    )
    parser.add_argument(
        "--orthographic",
        action="store_true",
        help="use an orthographic camera instead of the default perspective",
    )
    parser.add_argument(
        "--margin",
        type=float,
        default=1.10,
        metavar="F",
        help="camera framing margin (default: %(default)s)",
    )
    parser.add_argument(
        "--samples",
        type=int,
        default=64,
        metavar="N",
        help="Cycles samples (default: %(default)s)",
    )
    parser.add_argument(
        "--width",
        type=int,
        default=1600,
        metavar="PX",
        help="image width (default: %(default)s)",
    )
    parser.add_argument(
        "--height",
        type=int,
        default=1200,
        metavar="PX",
        help="image height (default: %(default)s)",
    )
    args = parser.parse_args(cli.script_arguments(argv))

    if args.samples < 1 or args.width < 1 or args.height < 1:
        parser.error("--samples, --width and --height must be at least 1")
    if not -90.0 <= args.elevation <= 90.0:
        parser.error("--elevation must be between -90 and 90 degrees")
    if args.margin <= 0.0:
        parser.error("--margin must be positive")
    for name in (
        "spread",
        "gap",
        "plate_gap",
        "plate_half_gap",
        "cover_gap",
    ):
        if getattr(args, name) < 0.0:
            parser.error(f"--{name.replace('_', '-')} must be non-negative")
    return args


def _local_z_range(obj):
    """Lowest and highest vertex in the object's local Z."""

    values = [vertex.co.z for vertex in obj.data.vertices]
    if not values:
        raise RuntimeError(f"{obj.name}: empty mesh")
    return min(values), max(values)


def _place_bottom(obj, bottom_z):
    """Move ``obj`` vertically so its lowest point sits at ``bottom_z``."""

    low, high = _local_z_range(obj)
    obj.location.z = bottom_z - low
    return high - low


def _edge_parts(keys, case_edges):
    """Return ``(suffix, object)`` edge-ring parts, split when wide."""

    metrics = case_module.case_metrics(keys)
    if not case_split.should_split(metrics):
        return [("whole", case_edges)]
    pieces = case_split.split_case_edges(
        case_edges, metrics, name="exploded_case_edges"
    )
    # The original closed ring is only a source for the split; do not render it.
    case_edges.hide_render = True
    return pieces


def _plate_parts(top_plate, split_plate):
    """Return the plate layer and whether it was split."""

    if not split_plate:
        return [("whole", top_plate)], False
    top_half, bottom_half = plate_module.split_top_plate(
        top_plate, name="exploded_top_plate"
    )
    top_plate.hide_render = True
    # Bottom half first so the two halves read from the underside up.
    return [("bottom", bottom_half), ("top", top_half)], True


def _frame_camera(scene, objects, args):
    """Frame the exploded assembly with a three-quarter camera."""

    bpy.context.view_layer.update()
    corners = [
        obj.matrix_world @ Vector(point)
        for obj in objects
        for point in obj.bound_box
    ]
    if not corners:
        raise RuntimeError("nothing to render")

    minimum = Vector((min(c[i] for c in corners) for i in range(3)))
    maximum = Vector((max(c[i] for c in corners) for i in range(3)))
    centre = (minimum + maximum) / 2.0
    radius = max((corner - centre).length for corner in corners)
    if radius <= 1e-9:
        raise RuntimeError("exploded assembly has no size")

    azimuth = math.radians(args.azimuth)
    elevation = math.radians(args.elevation)
    direction = Vector(
        (
            math.cos(elevation) * math.sin(azimuth),
            -math.cos(elevation) * math.cos(azimuth),
            math.sin(elevation),
        )
    )

    camera_data = bpy.data.cameras.new("exploded_camera")
    camera_data.lens = 50.0
    camera_data.clip_start = 0.1
    camera_data.clip_end = 10000.0
    camera = bpy.data.objects.new("exploded_camera", camera_data)
    scene.collection.objects.link(camera)
    scene.camera = camera

    if args.orthographic:
        camera_data.type = "ORTHO"
        camera_data.ortho_scale = 2.0 * radius * args.margin
        distance = radius * 4.0 + 100.0
    else:
        distance = (
            radius / math.tan(camera_data.angle / 2.0) * args.margin
        )
    camera.location = centre + direction * distance
    preview.look_at(camera, centre)

    # A camera-aligned fill keeps the undersides of the spread pieces readable;
    # the preview's three suns all come from above.
    fill_data = bpy.data.lights.new("exploded_fill", "SUN")
    fill_data.energy = 1.5
    fill_data.angle = math.radians(35.0)
    fill = bpy.data.objects.new("exploded_fill", fill_data)
    scene.collection.objects.link(fill)
    fill.rotation_euler = camera.rotation_euler
    return camera


def main(argv=None):
    args = parse_args(argv)
    keys = preview.load_keyboard(args.keyboard)
    name = args.keyboard

    output_path = args.output or os.path.join(
        PROJECT_DIR, "build", f"{name}_exploded.png"
    )
    output_path = os.path.abspath(output_path)
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    export.reset_scene()
    top_plate, case_bottom, case_edges, cover_obj = preview.build_parts(keys)

    edge_parts = _edge_parts(keys, case_edges)
    plate_parts, split_plate = _plate_parts(top_plate, args.split_plate)

    # Bottom tray at the bottom.
    bottom_height = _place_bottom(case_bottom, 0.0)
    bottom_top = bottom_height

    # Side U-pieces spread away from the rails along X.  The two middle rails
    # stay at their wall positions, otherwise the north rail ends up hidden
    # behind the top plate.
    edge_lift = bottom_top + args.gap
    edge_top = edge_lift
    for suffix, piece in edge_parts:
        height = _place_bottom(piece, edge_lift)
        edge_top = max(edge_top, edge_lift + height)
        if suffix == "west":
            piece.location.x -= args.spread
        elif suffix == "east":
            piece.location.x += args.spread

    # Top plate above the edge ring.  A split plate shows its two halves with a
    # small gap between them.
    plate_base = edge_top + args.plate_gap
    if not split_plate:
        plate_height = _place_bottom(top_plate, plate_base)
        plate_top = plate_base + plate_height
    else:
        bottom_half = dict(plate_parts)["bottom"]
        top_half = dict(plate_parts)["top"]
        bottom_height = _place_bottom(bottom_half, plate_base)
        top_base = plate_base + bottom_height + args.plate_half_gap
        top_height = _place_bottom(top_half, top_base)
        plate_top = top_base + top_height

    # Cover at the very top.
    cover_base = plate_top + args.cover_gap
    cover_height = _place_bottom(cover_obj, cover_base)
    cover_top = cover_base + cover_height

    for _, piece in edge_parts:
        preview.apply_material(piece, preview.CASE_COLOUR, roughness=0.45)
    preview.apply_material(
        case_bottom, preview.CASE_BOTTOM_COLOUR, roughness=0.45
    )
    for _, piece in plate_parts:
        preview.apply_material(piece, preview.PLATE_COLOUR, roughness=0.5)
    preview.apply_material(cover_obj, preview.COVER_COLOUR, roughness=0.4)

    render_objects = [case_bottom]
    render_objects.extend(piece for _, piece in edge_parts)
    render_objects.extend(piece for _, piece in plate_parts)
    render_objects.append(cover_obj)

    scene = preview.configure_scene(args)
    _frame_camera(scene, render_objects, args)

    scene.render.filepath = output_path
    bpy.ops.render.render(write_still=True)

    print(
        f"{name}: bottom 0..{bottom_top:.1f}, "
        f"edges {edge_lift:.1f}..{edge_top:.1f} "
        f"({len(edge_parts)} piece(s), sides spread {args.spread:.1f} mm), "
        f"plate {plate_base:.1f}..{plate_top:.1f} "
        f"({'two halves' if split_plate else 'one piece'}), "
        f"cover {cover_base:.1f}..{cover_top:.1f} mm"
    )
    print(f"Rendered {output_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
