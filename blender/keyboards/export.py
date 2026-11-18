"""Scene reset and binary STL export."""

from __future__ import annotations

import os

import bpy


def reset_scene():
    """Start from an empty scene, independent of Blender's startup file."""

    bpy.ops.wm.read_factory_settings(use_empty=True)


def _export_to(obj, path):
    """Run the version-appropriate STL exporter for ``obj``."""

    bpy.ops.object.select_all(action="DESELECT")
    obj.select_set(True)
    bpy.context.view_layer.objects.active = obj

    if hasattr(bpy.ops.wm, "stl_export"):
        bpy.ops.wm.stl_export(
            filepath=path,
            export_selected_objects=True,
            ascii_format=False,
            apply_modifiers=True,
        )
    else:
        bpy.ops.export_mesh.stl(
            filepath=path,
            use_selection=True,
            ascii=False,
            apply_modifiers=True,
        )


def export_stl(obj, path):
    """Export ``obj`` to a binary STL and verify that it was written.

    The export goes to a temporary file in the destination directory and is
    moved into place only after it has been verified, so a failed export never
    destroys a previously good artifact.
    """

    path = os.path.abspath(path)
    directory = os.path.dirname(path)
    os.makedirs(directory, exist_ok=True)

    temporary = os.path.join(
        directory,
        f".{os.path.basename(path)}.{os.getpid()}.tmp.stl",
    )
    if os.path.exists(temporary):
        os.remove(temporary)

    try:
        _export_to(obj, temporary)
        if not os.path.isfile(temporary) or os.path.getsize(temporary) == 0:
            raise RuntimeError(f"STL export failed, nothing written to {path}")
        os.replace(temporary, path)
    finally:
        if os.path.exists(temporary):
            os.remove(temporary)

    print(f"Exported {path} ({os.path.getsize(path)} bytes)")
