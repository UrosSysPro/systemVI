"""Render an assembled keyboard: top plate, case and cover.

Monokai light green palette, same three-quarter view as the earlier
``build/preview_iso.png`` preview.

Run headlessly with:
    blender --background --python keyboards/render_preview.py

Output: build/kb75_monokai.png
"""

import importlib.util
import math
import os
import sys

import bpy
from mathutils import Vector

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_DIR = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, SCRIPT_DIR)

import keyboard_case as kc
import keyboard_parts as kb

# Reuse the kb75 layout instead of duplicating it here.
_spec = importlib.util.spec_from_file_location(
    "kb75_main", os.path.join(SCRIPT_DIR, "kb75", "main.py")
)
kb75 = importlib.util.module_from_spec(_spec)
_spec.loader.exec_module(kb75)

OUTPUT_PATH = os.path.join(PROJECT_DIR, "build", "kb75_monokai.png")

# The configuration from the "kb75_tall" command.
PARAMS = kb.PlateParams(
    margin_left=6.0,
    margin_right=6.0,
    margin_top=5.0,
    margin_bottom=8.0,
    led_facing=kb.LED_FACING_SOUTH,
)
COMPARTMENT_HEIGHT = 24.0
COMPARTMENT_RAMP = 20.0

# Monokai palette, sRGB hex.
GREEN = "#A6E22E"
GREEN_LIGHT = "#C7EF6B"
PAPER = "#EDEBE1"
BACKGROUND = "#F2F1EA"


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


# --------------------------------------------------------------------------
# Build the three parts and put them together
# --------------------------------------------------------------------------

kb.reset_scene()
keys = kb.keys_from_rows(kb75.LAYOUT_ROWS)

plate = kb.build_top_plate(keys, name="kb75_top_plate", params=PARAMS)
case = kc.build_case(
    keys,
    name="kb75_case",
    params=PARAMS,
    compartment_height=COMPARTMENT_HEIGHT,
    compartment_ramp=COMPARTMENT_RAMP,
)
cover = kc.build_cover(
    keys, name="kb75_cover", params=PARAMS, compartment_height=COMPARTMENT_HEIGHT
)

# The exported plate sits on z = 0; in the assembly it rests on the case floor.
plate.location.z = kc.FLOOR

apply_material(plate, PAPER, roughness=0.45)
apply_material(case, GREEN, roughness=0.5)
apply_material(cover, GREEN_LIGHT, roughness=0.5)

bpy.context.view_layer.update()

# --------------------------------------------------------------------------
# Scene, lights, camera
# --------------------------------------------------------------------------

scene = bpy.context.scene
scene.render.engine = "CYCLES"
scene.cycles.device = "CPU"
scene.cycles.samples = 64
scene.cycles.use_denoising = True
scene.render.resolution_x = 1600
scene.render.resolution_y = 1200
scene.render.film_transparent = False
scene.render.image_settings.file_format = "PNG"
scene.view_settings.view_transform = "Standard"
scene.view_settings.exposure = -0.35
scene.view_settings.look = "None"

world = bpy.data.worlds.new("world")
world.use_nodes = True
background = world.node_tree.nodes["Background"]
background.inputs[0].default_value = _srgb(BACKGROUND)
background.inputs[1].default_value = 1.1
scene.world = world

key = bpy.data.lights.new("key", "SUN")
key.energy = 1.7
key.angle = math.radians(12.0)
key_obj = bpy.data.objects.new("key", key)
scene.collection.objects.link(key_obj)
key_obj.rotation_euler = (math.radians(48), math.radians(12), math.radians(35))

fill = bpy.data.lights.new("fill", "SUN")
fill.energy = 0.45
fill.angle = math.radians(35.0)
fill_obj = bpy.data.objects.new("fill", fill)
scene.collection.objects.link(fill_obj)
fill_obj.rotation_euler = (math.radians(65), 0.0, math.radians(-120))

# Bounding sphere of the assembly, used to frame the camera.
corners = []
for obj in (plate, case, cover):
    for corner in obj.bound_box:
        corners.append(obj.matrix_world @ Vector(corner))

minimum = Vector((min(c[i] for c in corners) for i in range(3)))
maximum = Vector((max(c[i] for c in corners) for i in range(3)))
centre = (minimum + maximum) / 2.0
radius = max((corner - centre).length for corner in corners)

camera_data = bpy.data.cameras.new("camera")
camera_data.lens = 50.0
camera = bpy.data.objects.new("camera", camera_data)
scene.collection.objects.link(camera)
scene.camera = camera

# Same view direction as build/preview_iso.png.
view_direction = Vector((190.0, -260.0, 184.0)).normalized()
distance = radius / math.tan(camera_data.angle / 2.0) * 1.12
camera.location = centre + view_direction * distance
look_at(camera, centre)

scene.render.filepath = OUTPUT_PATH
bpy.ops.render.render(write_still=True)
print(f"Rendered {OUTPUT_PATH}")
