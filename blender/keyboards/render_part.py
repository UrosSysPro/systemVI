"""Render any generated part, or an arbitrary STL, from any orbit angle.

Examples
--------

Top view of the edge ring interior::

    blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
        --keyboard kbNumpad --part case_edges --azimuth 35 --elevation 35

Underside of the cover (magnet bosses).  Look from the north or side: the
front lip hides the underside from a low south view::

    blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
        --keyboard kbNumpad --part cover --azimuth 180 --elevation -55

Bottom view of the plate::

    blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
        --keyboard kb60 --part plate --azimuth 0 --elevation -60

An exported STL::

    blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
        --stl build/kbNumpad/case/case_edges.stl --azimuth 70 --elevation 25

Azimuth 0 looks from the south (front), 90 from the east, 180 from the north,
270 from the west.  Elevation is degrees above the XY plane; negative values
look from below.  The camera automatically frames the selected part.
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
import cli
import cover as cover_module
import export
import layout
import mounting
import plate as plate_module
import stabilizer
import render_preview as preview

BUILD_DIR = os.path.join(PROJECT_DIR, "build")

PART_COLOURS = {
    "plate": preview.PLATE_COLOUR,
    "case": preview.CASE_COLOUR,
    "case_bottom": preview.CASE_BOTTOM_COLOUR,
    "case_edges": preview.CASE_COLOUR,
    "cover": preview.COVER_COLOUR,
    "slider": "#A6E22E",
}


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--keyboard",
        choices=sorted(preview.KEYBOARDS),
        default="kbNumpad",
        help="keyboard layout to build (default: %(default)s)",
    )
    parser.add_argument(
        "--part",
        choices=(
            "plate",
            "case",
            "case_bottom",
            "case_edges",
            "cover",
            "assembly",
            "slider",
        ),
        default="assembly",
        help="which part to build; ignored with --stl (default: %(default)s)",
    )
    parser.add_argument(
        "--stl",
        metavar="PATH",
        help="render an existing STL instead of building a part",
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
        default=25.0,
        metavar="DEG",
        help="angle above the XY plane; negative looks from below "
        "(default: %(default)s)",
    )
    parser.add_argument(
        "--margin",
        type=float,
        default=1.12,
        metavar="F",
        help="camera framing margin (default: %(default)s)",
    )
    parser.add_argument(
        "--orthographic",
        action="store_true",
        help="use an orthographic camera instead of the default perspective",
    )
    parser.add_argument(
        "--samples",
        type=int,
        default=24,
        metavar="N",
        help="Cycles samples (default: %(default)s)",
    )
    parser.add_argument(
        "--width",
        type=int,
        default=1200,
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
        "--colour",
        metavar="HEX",
        help="override material colour, e.g. #E6DB74",
    )
    parser.add_argument(
        "--output",
        metavar="PATH",
        help="PNG path; default build/<part>_az<angle>_el<angle>.png",
    )
    args = parser.parse_args(cli.script_arguments(argv))

    if args.samples < 1 or args.width < 1 or args.height < 1:
        parser.error("--samples, --width and --height must be at least 1")
    if not -90.0 <= args.elevation <= 90.0:
        parser.error("--elevation must be between -90 and 90 degrees")
    if args.margin <= 0.0:
        parser.error("--margin must be positive")
    if args.stl and not os.path.isfile(args.stl):
        parser.error(f"--stl file not found: {args.stl}")
    return args


def _tag(value):
    """Filesystem-friendly angle tag."""

    return f"{value:g}".replace("-", "m").replace(".", "p")


def _load_stl(path):
    if hasattr(bpy.ops.wm, "stl_import"):
        bpy.ops.wm.stl_import(filepath=path)
    else:
        bpy.ops.import_mesh.stl(filepath=path)
    objects = [obj for obj in bpy.context.scene.objects if obj.type == "MESH"]
    if not objects:
        raise RuntimeError(f"no mesh imported from {path}")
    return objects


def _build(keyboard, part):
    options = mounting.MountingOptions()
    params = layout.PlateParams()
    name = f"view_{keyboard}_{part}"

    if part == "slider":
        return [stabilizer.build_slider(name=name)]

    keys = preview.load_keyboard(keyboard)
    if part == "plate":
        return [
            plate_module.build_top_plate(keys, name=name, params=params)
        ]
    if part == "case_bottom":
        return [
            case_module.build_case_bottom(
                keys, name=name, params=params, options=options
            )
        ]
    if part == "case_edges":
        return [
            case_module.build_case_edges(
                keys, name=name, params=params, options=options
            )
        ]
    if part == "case":
        return [
            case_module.build_case_bottom(
                keys, name=f"{name}_bottom", params=params, options=options
            ),
            case_module.build_case_edges(
                keys, name=f"{name}_edges", params=params, options=options
            ),
        ]
    if part == "cover":
        return [
            cover_module.build_cover(
                keys, name=name, params=params, options=options
            )
        ]

    top_plate = plate_module.build_top_plate(
        keys, name=f"{name}_plate", params=params
    )
    bottom = case_module.build_case_bottom(
        keys, name=f"{name}_case_bottom", params=params, options=options
    )
    edges = case_module.build_case_edges(
        keys, name=f"{name}_case_edges", params=params, options=options
    )
    cover_obj = cover_module.build_cover(
        keys, name=f"{name}_cover", params=params, options=options
    )
    top_plate.location.z = case_module.FLOOR
    return [top_plate, bottom, edges, cover_obj]


def _apply_materials(objects, part, args):
    if args.colour:
        for obj in objects:
            preview.apply_material(obj, args.colour)
        return
    if part == "assembly" and len(objects) == 4:
        colours = (
            PART_COLOURS["plate"],
            PART_COLOURS["case_bottom"],
            PART_COLOURS["case_edges"],
            PART_COLOURS["cover"],
        )
        for obj, colour in zip(objects, colours):
            preview.apply_material(obj, colour)
        return
    if part == "case" and len(objects) == 2:
        preview.apply_material(objects[0], PART_COLOURS["case_bottom"])
        preview.apply_material(objects[1], PART_COLOURS["case_edges"])
        return
    colour = PART_COLOURS.get(part, preview.PLATE_COLOUR)
    for obj in objects:
        preview.apply_material(obj, colour)


def _frame_camera(scene, objects, args):
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
        raise RuntimeError("selected geometry has no size")

    azimuth = math.radians(args.azimuth)
    elevation = math.radians(args.elevation)
    direction = Vector(
        (
            math.cos(elevation) * math.sin(azimuth),
            -math.cos(elevation) * math.cos(azimuth),
            math.sin(elevation),
        )
    )

    camera_data = bpy.data.cameras.new("part_camera")
    camera_data.lens = 50.0
    camera_data.clip_start = 0.1
    camera_data.clip_end = 10000.0
    camera = bpy.data.objects.new("part_camera", camera_data)
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

    # A camera-aligned fill keeps underside views readable; the preview's
    # three suns all come from above.
    fill_data = bpy.data.lights.new("view_fill", "SUN")
    fill_data.energy = 1.4
    fill_data.angle = math.radians(35.0)
    fill = bpy.data.objects.new("view_fill", fill_data)
    scene.collection.objects.link(fill)
    fill.rotation_euler = camera.rotation_euler
    return camera


def _output_path(args):
    if args.output:
        return os.path.abspath(args.output)
    if args.stl:
        stem = os.path.splitext(os.path.basename(args.stl))[0]
        return os.path.join(
            BUILD_DIR,
            f"{stem}_az{_tag(args.azimuth)}_el{_tag(args.elevation)}.png",
        )
    return os.path.join(
        BUILD_DIR,
        f"{args.keyboard}_{args.part}_az{_tag(args.azimuth)}"
        f"_el{_tag(args.elevation)}.png",
    )


def main(argv=None):
    args = parse_args(argv)
    export.reset_scene()

    if args.stl:
        objects = _load_stl(os.path.abspath(args.stl))
        part = "stl"
    else:
        objects = _build(args.keyboard, args.part)
        part = args.part

    _apply_materials(objects, part, args)
    scene = preview.configure_scene(args)
    _frame_camera(scene, objects, args)

    output = _output_path(args)
    os.makedirs(os.path.dirname(output), exist_ok=True)
    scene.render.filepath = output
    bpy.ops.render.render(write_still=True)

    for obj in objects:
        dimensions = tuple(round(value, 2) for value in obj.dimensions)
        print(
            f"{obj.name}: {dimensions[0]} x {dimensions[1]} x "
            f"{dimensions[2]} mm"
        )
    print(f"Rendered {output}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
