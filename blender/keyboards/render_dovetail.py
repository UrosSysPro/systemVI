"""Render close-ups of one edge-ring dovetail.

Two images are written:

- the male rail's end with the dovetail plug, viewed from the east,
- the matching female side piece with the slot, viewed from the west.

The plug's lower end and the slot's open bottom / top cap are therefore both
visible.  The male rail is yellow and the female side piece is cyan.

Run headlessly with:

    blender --background --python-exit-code 1 --python keyboards/render_dovetail.py -- \
        --keyboard kb60 --side north

Options:

- ``--keyboard kbNumpad|kb60|kb75|kb100``
- ``--side north|south`` — which wall's dovetail (default north)
- ``--samples N``, ``--width PX``, ``--height PX``
- ``--output BASE`` — optional base path; the script appends ``_male`` and
  ``_female`` before ``.png``.  Defaults to
  ``build/<keyboard>_dovetail_<side>_male.png`` and ``_female.png``.

The script builds the same default parts as the generator, splits the edge ring
and writes only PNGs.
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
import render_preview as preview


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--keyboard",
        choices=sorted(preview.KEYBOARDS),
        default="kb60",
        help="keyboard layout to render (default: %(default)s)",
    )
    parser.add_argument(
        "--side",
        choices=("north", "south"),
        default="north",
        help="which wall's dovetail (default: %(default)s)",
    )
    parser.add_argument(
        "--samples",
        type=int,
        default=32,
        metavar="N",
        help="Cycles samples (default: %(default)s)",
    )
    parser.add_argument(
        "--width",
        type=int,
        default=1400,
        metavar="PX",
        help="image width (default: %(default)s)",
    )
    parser.add_argument(
        "--height",
        type=int,
        default=900,
        metavar="PX",
        help="image height (default: %(default)s)",
    )
    parser.add_argument(
        "--output",
        metavar="BASE",
        help="optional base PNG path; _male/_female are appended",
    )
    args = parser.parse_args(cli.script_arguments(argv))
    if args.samples < 1 or args.width < 1 or args.height < 1:
        parser.error("--samples, --width and --height must be at least 1")
    return args


def _output_paths(args):
    if args.output:
        base = os.path.abspath(args.output)
        stem, extension = os.path.splitext(base)
        if not extension:
            extension = ".png"
        return (
            f"{stem}_male{extension}",
            f"{stem}_female{extension}",
        )
    folder = os.path.join(PROJECT_DIR, "build")
    return (
        os.path.join(folder, f"{args.keyboard}_dovetail_{args.side}_male.png"),
        os.path.join(folder, f"{args.keyboard}_dovetail_{args.side}_female.png"),
    )


def _render_closeup(scene, obj, target, direction, output):
    for other in scene.objects:
        if other.type == "MESH":
            other.hide_render = other is not obj
    obj.hide_render = False

    for existing in list(scene.objects):
        if existing.type in {"CAMERA", "LIGHT"} and existing.name.startswith(
            "dovetail_"
        ):
            kind = existing.type
            data = existing.data
            bpy.data.objects.remove(existing, do_unlink=True)
            if kind == "CAMERA":
                bpy.data.cameras.remove(data)
            else:
                bpy.data.lights.remove(data)

    bpy.context.view_layer.update()
    camera_data = bpy.data.cameras.new("dovetail_camera")
    camera_data.lens = 55.0
    camera_data.clip_start = 0.1
    camera_data.clip_end = 10000.0
    camera = bpy.data.objects.new("dovetail_camera", camera_data)
    scene.collection.objects.link(camera)
    scene.camera = camera
    camera.location = Vector(target) + Vector(direction).normalized() * 150.0
    preview.look_at(camera, target)

    fill_data = bpy.data.lights.new("dovetail_fill", "SUN")
    fill_data.energy = 3.0
    fill_data.angle = math.radians(40.0)
    fill = bpy.data.objects.new("dovetail_fill", fill_data)
    scene.collection.objects.link(fill)
    fill.rotation_euler = camera.rotation_euler

    scene.render.filepath = output
    bpy.ops.render.render(write_still=True)
    print(f"Rendered {output}")


def main(argv=None):
    args = parse_args(argv)
    side = args.side
    keys = preview.load_keyboard(args.keyboard)
    male_output, female_output = _output_paths(args)
    for path in (male_output, female_output):
        os.makedirs(os.path.dirname(path), exist_ok=True)

    export.reset_scene()
    top_plate, case_bottom, case_edges, cover_obj = preview.build_parts(keys)
    for obj in (top_plate, case_bottom, cover_obj):
        obj.hide_render = True

    metrics = case_module.case_metrics(keys)
    pieces = dict(
        case_split.split_case_edges(
            case_edges, metrics, name="dovetail_edges"
        )
    )
    case_edges.hide_render = True
    male = pieces[side]
    female = pieces["east"]
    for suffix, piece in pieces.items():
        if piece not in (male, female):
            piece.hide_render = True

    y0, y1, z_top = case_split._wall_band(metrics, side)
    y_centre = (y0 + y1) / 2.0
    _, east_cut = case_split.split_positions(metrics.width)
    plug_top = z_top - case_split.DOVETAIL_TOP_GAP
    joint_mid = (case_split.JOINT_BOTTOM + plug_top) / 2.0

    preview.apply_material(male, preview.PLATE_COLOUR, roughness=0.5)
    preview.apply_material(female, preview.CASE_COLOUR, roughness=0.45)

    scene = preview.configure_scene(args)
    _render_closeup(
        scene,
        male,
        (east_cut + 2.0, y_centre, joint_mid),
        (1.0, -0.8, 0.5),
        male_output,
    )
    _render_closeup(
        scene,
        female,
        (east_cut - 1.0, y_centre, joint_mid),
        (-1.0, -0.8, 0.5),
        female_output,
    )

    print(
        f"{args.keyboard} {side} dovetail: plug "
        f"{case_split.JOINT_BOTTOM:.2f}..{plug_top:.2f} mm, slot open at "
        f"the bottom, cap at the top (wall top {z_top:.2f} mm)"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
