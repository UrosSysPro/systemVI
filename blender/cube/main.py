"""Generate a cube and export it as cube.stl.

Run headlessly with:
    blender --background --python cube/main.py
"""

import os
import sys

import bpy

# Side length of the cube in millimetres (1 Blender unit == 1 mm on export).
CUBE_SIZE = 20.0

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
OUTPUT_PATH = os.path.join(SCRIPT_DIR, "cube.stl")


def reset_scene():
    """Start from an empty scene, independent of Blender's startup file."""
    bpy.ops.wm.read_factory_settings(use_empty=True)


def create_cube(size):
    """Create a cube with the given side length, centred at the origin."""
    bpy.ops.mesh.primitive_cube_add(size=size, location=(0.0, 0.0, 0.0))
    obj = bpy.context.active_object
    obj.name = "Cube"
    obj.data.name = "CubeMesh"

    # Bake the transform into the mesh so the exported STL is exactly `size` mm.
    bpy.ops.object.transform_apply(location=False, rotation=True, scale=True)

    # A cube is watertight by construction; validate explicitly anyway.
    obj.data.validate(verbose=False)
    return obj


def export_stl(path):
    """Export the current scene to a binary STL file."""
    directory = os.path.dirname(path)
    os.makedirs(directory, exist_ok=True)
    if os.path.exists(path):
        os.remove(path)

    if hasattr(bpy.ops.wm, "stl_export"):
        # Blender 4.2+ (new STL I/O).
        bpy.ops.wm.stl_export(
            filepath=path,
            export_selected_objects=False,
            ascii_format=False,
            apply_modifiers=True,
        )
    else:
        # Blender <= 4.1.
        bpy.ops.export_mesh.stl(
            filepath=path,
            use_selection=False,
            ascii=False,
            apply_modifiers=True,
        )


def main():
    reset_scene()
    cube = create_cube(CUBE_SIZE)

    dimensions = tuple(round(d, 3) for d in cube.dimensions)
    print(f"Cube created: {CUBE_SIZE} mm side, dimensions {dimensions} mm")

    export_stl(OUTPUT_PATH)

    if not os.path.isfile(OUTPUT_PATH) or os.path.getsize(OUTPUT_PATH) == 0:
        print(f"ERROR: export failed, no STL written to {OUTPUT_PATH}", file=sys.stderr)
        return 1

    print(f"Exported {OUTPUT_PATH} ({os.path.getsize(OUTPUT_PATH)} bytes)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
