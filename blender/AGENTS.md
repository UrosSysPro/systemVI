# AGENTS.md

Instructions for this repository: a collection of small, self-contained Blender
scripts, each of which generates a 3D-printable model and saves it as an STL file.

## Purpose

This project contains multiple subprojects. Every subproject produces exactly one
3D model that is meant to be 3D printed. All work is headless — there is no GUI
step, no manual clicking, no `.blend` file to open and inspect.

## Golden rules

1. **Every script does the same two things**: build a mesh in Blender and export
   it to an STL file. Nothing else is required of a model script.
2. **Scripts always run headlessly**, i.e. through `blender --background --python
   <script>.py`. A script must never depend on an interactive UI, a viewport, an
   active window, or user input. Assume no display is available.
3. **One subproject per model.** Each model lives in its own top-level directory
   containing a `main.py` entry point (other helper modules may be added inside
   that directory only if they are genuinely useful).
4. **The STL output is a build artifact, not source.** Generated STL files are
   git-ignored and must never be committed.
5. **A run must be reproducible**: the same script on the same Blender version
   must produce the same model, with no randomness unless explicitly seeded.

## Directory layout

```
<project-root>/
├── AGENTS.md          # this file
├── .gitignore         # ignores generated STL files
└── <model-name>/      # one directory per subproject
    └── main.py        # builds the model and writes <model-name>.stl
```

The existing subproject is `cube/`, which builds a cube and writes `cube.stl`.

## Writing a new model script

A model script (`<model-name>/main.py`) must, in order:

1. **Reset the scene.** Start from an empty scene so results do not depend on
   Blender's startup file (`bpy.ops.wm.read_factory_settings(use_empty=True)`).
2. **Build the geometry** procedurally with `bpy` / `bmesh`. Prefer exact,
   parametric dimensions (constants at the top of the file) over magic numbers
   scattered through the code.
3. **Prepare the mesh for printing**: apply object transforms (scale, rotation),
   keep the mesh watertight/manifold, avoid non-manifold edges, self-intersections
   and zero-area faces, and model in millimetres at real-world scale (1 Blender
   unit = 1 mm in the exported STL).
4. **Export STL** to a path derived from the script location, so the command works
   from any working directory. Write the file next to the script and name it after
   the subproject, e.g. `cube/cube.stl`.
5. **Report and fail loudly.** Print the absolute output path on success and
   verify the file exists and is non-empty. Exit with a non-zero status
   (`sys.exit(1)` / raising an exception) on any failure, so CI and callers can
   detect it.

Export must work across Blender versions. Prefer `bpy.ops.wm.stl_export(...)` and
fall back to `bpy.ops.export_mesh.stl(...)` on older releases, as `cube/main.py`
demonstrates. Export binary STL, not ASCII.

## Running

Run any subproject from the repository root:

```sh
blender --background --python <model-name>/main.py
```

Example:

```sh
blender --background --python cube/main.py
```

The script must not depend on the current working directory, and must not open a
window or wait for input. Passing extra arguments after `--` is allowed
(`blender --background --python cube/main.py -- --size 30`) if a script opts into
argument parsing; keep defaults sensible so the bare command always works.

## Conventions

- Python 3, Blender's bundled `bpy` and standard library only. Do not add third
  party Python dependencies and do not require a virtual environment.
- Keep each `main.py` runnable top-to-bottom without side effects on import of
  other subprojects.
- Top-of-file constants for dimensions, in millimetres.
- Add a short module docstring stating what the model is and what it writes.
- Print a one-line status message per run (`Exported <path>`) and one on failure.

## Git hygiene

- `.gitignore` ignores every `*.stl` file in the repository.
- Commit sources (`AGENTS.md`, `.gitignore`, `*/main.py`), never generated models.
- Do not commit Blender backups (`*.blend1`) or caches.

## Definition of done for a new subproject

- `<model-name>/main.py` exists and runs headlessly.
- `blender --background --python <model-name>/main.py` exits with status 0.
- `<model-name>/<model-name>.stl` is created, is non-empty, and is ignored by git.
- The mesh is watertight and dimensionally correct for 3D printing.
