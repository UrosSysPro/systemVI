"""Render an assembled keyboard: top plate, case and cover.

The keyboard is selected on the command line and the three printed parts use
three distinct Monokai accent colours.  The script builds the same parts as
the generator, so the render matches the exported STLs.

Run headlessly with:
    blender --background --python keyboards/render_preview.py -- --keyboard kb75

Available keyboards: kbNumpad, kb60, kb75, kb100.

Output defaults to build/<keyboard>_preview.png; override with --output.
"""

from __future__ import annotations

import argparse
import importlib.util
import math
import os
import sys

import bpy
from mathutils import Vector

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, SCRIPT_DIR)

import case as case_module
import cli
import cover as cover_module
import export
import layout
import plate as plate_module

KEYBOARDS = {
    "kbNumpad": os.path.join(SCRIPT_DIR, "kbNumpad", "main.py"),
    "kb60": os.path.join(SCRIPT_DIR, "kb60", "main.py"),
    "kb75": os.path.join(SCRIPT_DIR, "kb75", "main.py"),
    "kb100": os.path.join(SCRIPT_DIR, "kb100", "main.py"),
}

# Monokai palette.
CASE_COLOUR = "#66D9EF"      # cyan
COVER_COLOUR = "#F92672"     # pink
PLATE_COLOUR = "#E6DB74"     # yellow
BACKGROUND = "#272822"       # Monokai background


def _srgb(hex_colour):
    """Hex sRGB to a linear RGBA tuple for Blender."""

    value = hex_colour.lstrip("#")
    channels = [int(value[i : i + 2], 16) / 255.0 for i in (0, 2, 4)]

    def linear(channel):
        if channel <= 0.04045:
            return channel / 12.92
        return ((channel + 0.055) / 1.055) ** 2.4

    return tuple(linear(channel) for channel in channels) + (1.0,)


def apply_material(obj, colour, roughness=0.55):
    material = bpy.data.materials.new(obj.name + "_material")
    material.use_nodes = True
    bsdf = material.node_tree.nodes["Principled BSDF"]
    bsdf.inputs["Base Color"].default_value = _srgb(colour)
    bsdf.inputs["Roughness"].default_value = roughness

    # Boolean results can leave empty material slots, which would hide the
    # material we append, so rebuild the slot list from scratch.
    obj.data.materials.clear()
    obj.data.materials.append(material)
    for polygon in obj.data.polygons:
        polygon.material_index = 0


def look_at(obj, target):
    direction = Vector(target) - obj.location
    obj.rotation_euler = direction.to_track_quat("-Z", "Y").to_euler()


def load_keyboard(name):
    """Load an entry-point module and return its ``KEYS`` layout."""

    path = KEYBOARDS[name]
    spec = importlib.util.spec_from_file_location(f"{name}_preview_source", path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module.KEYS


def build_parts(keys):
    """Build the three parts in the same relative positions as the STLs."""

    params = layout.PlateParams()
    top_plate = plate_module.build_top_plate(
        keys, name="preview_top_plate", params=params
    )
    case_obj = case_module.build_case(
        keys, name="preview_case", params=params
    )
    cover_obj = cover_module.build_cover(
        keys, name="preview_cover", params=params
    )

    # The exported plate sits on z = 0; in the assembly it rests on the floor.
    top_plate.location.z = case_module.FLOOR
    return top_plate, case_obj, cover_obj


def configure_scene(args):
    scene = bpy.context.scene
    scene.render.engine = "CYCLES"
    scene.cycles.device = "CPU"
    scene.cycles.samples = args.samples
    scene.cycles.use_denoising = True
    scene.render.resolution_x = args.width
    scene.render.resolution_y = args.height
    scene.render.resolution_percentage = 100
    scene.render.film_transparent = False
    scene.render.image_settings.file_format = "PNG"
    scene.view_settings.view_transform = "Standard"
    scene.view_settings.exposure = -0.15
    scene.view_settings.look = "None"

    world = bpy.data.worlds.new("world")
    world.use_nodes = True
    background = world.node_tree.nodes["Background"]
    background.inputs[0].default_value = _srgb(BACKGROUND)
    background.inputs[1].default_value = 1.0
    scene.world = world

    key = bpy.data.lights.new("key", "SUN")
    key.energy = 2.2
    key.angle = math.radians(12.0)
    key_obj = bpy.data.objects.new("key", key)
    scene.collection.objects.link(key_obj)
    key_obj.rotation_euler = (
        math.radians(48),
        math.radians(12),
        math.radians(35),
    )

    fill = bpy.data.lights.new("fill", "SUN")
    fill.energy = 0.7
    fill.angle = math.radians(35.0)
    fill_obj = bpy.data.objects.new("fill", fill)
    scene.collection.objects.link(fill_obj)
    fill_obj.rotation_euler = (math.radians(65), 0.0, math.radians(-120))

    rim = bpy.data.lights.new("rim", "SUN")
    rim.energy = 1.0
    rim.angle = math.radians(20.0)
    rim_obj = bpy.data.objects.new("rim", rim)
    scene.collection.objects.link(rim_obj)
    rim_obj.rotation_euler = (math.radians(70), 0.0, math.radians(155))

    return scene


def frame_camera(scene, parts):
    """Create a camera that frames the whole assembly."""

    corners = []
    for obj in parts:
        for corner in obj.bound_box:
            corners.append(obj.matrix_world @ Vector(corner))

    minimum = Vector((min(c[i] for c in corners) for i in range(3)))
    maximum = Vector((max(c[i] for c in corners) for i in range(3)))
    centre = (minimum + maximum) / 2.0
    radius = max((corner - centre).length for corner in corners)

    camera_data = bpy.data.cameras.new("camera")
    camera_data.lens = 50.0
    camera_data.clip_start = 1.0
    camera_data.clip_end = 10000.0
    camera = bpy.data.objects.new("camera", camera_data)
    scene.collection.objects.link(camera)
    scene.camera = camera

    view_direction = Vector((190.0, -260.0, 184.0)).normalized()
    distance = radius / math.tan(camera_data.angle / 2.0) * 1.15
    camera.location = centre + view_direction * distance
    look_at(camera, centre)
    return camera


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--keyboard",
        choices=sorted(KEYBOARDS),
        default="kb75",
        help="keyboard layout to render (default: %(default)s)",
    )
    parser.add_argument(
        "--output",
        metavar="PATH",
        help="PNG path (default: build/<keyboard>_preview.png)",
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
    if args.samples < 1:
        parser.error("--samples must be at least 1")
    if args.width < 1 or args.height < 1:
        parser.error("--width and --height must be positive")
    return args


def main(argv=None):
    args = parse_args(argv)
    keys = load_keyboard(args.keyboard)

    output_path = args.output or os.path.join(
        PROJECT_DIR, "build", f"{args.keyboard}_preview.png"
    )
    output_path = os.path.abspath(output_path)
    os.makedirs(os.path.dirname(output_path), exist_ok=True)

    export.reset_scene()
    top_plate, case_obj, cover_obj = build_parts(keys)

    apply_material(top_plate, PLATE_COLOUR, roughness=0.5)
    apply_material(case_obj, CASE_COLOUR, roughness=0.45)
    apply_material(cover_obj, COVER_COLOUR, roughness=0.4)

    scene = configure_scene(args)
    frame_camera(scene, (top_plate, case_obj, cover_obj))

    scene.render.filepath = output_path
    bpy.ops.render.render(write_still=True)
    print(f"Rendered {output_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
