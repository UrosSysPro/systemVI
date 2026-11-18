# Keyboard Generator

Headless Blender scripts that generate 3D-printable keyboard housings and
stabilizers. Every keyboard is built procedurally and exported as binary STL
files under `build/`; there is no GUI step and no `.blend` file to maintain.

Each keyboard produces three printed parts:

- **Top plate** — MX switch pockets, hotswap-socket pockets, LED pockets/light
  holes, and stabilizer post holes/wire channels.
- **Case** — hollow tray with an electronics bay, USB-C opening with a 45°
  beveled recess and internal glue shelf.
- **Cover** — flat or L-shaped lid for the electronics bay.

Custom stabilizer sliders are generated separately for use with 2 mm solid
copper wire.

## Implemented keyboards

- 60% — ANSI 61-key
- 75% — 83-key
- 100% — ANSI 104-key full-size
- Numpad — 17-key

Corne and Dactyl are planned; their entry points are placeholders.

## Requirements

- Blender 4.2 or newer; tested with Blender 5.1.
- No third-party Python packages.
- A `blender` executable on `PATH`, or use its full path.

## Quick start

Run from the repository root. Each command writes the top plate, case and cover
into `build/`:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py
blender --background --python-exit-code 1 --python keyboards/kb75/main.py
blender --background --python-exit-code 1 --python keyboards/kb100/main.py
blender --background --python-exit-code 1 --python keyboards/kbNumpad/main.py
```

The outputs are named:

```text
build/<name>_top_plate.stl
build/<name>_case.stl
build/<name>_cover.stl
```

Generate the custom stabilizer slider with:

```sh
blender --background --python-exit-code 1 --python keyboards/stabilizers/main.py
```

Print it twice per stabilized key. The script also prints the required copper
wire cut lengths.

Render a preview of a keyboard with:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb60
```

## Current defaults

- Top-plate padding: **2 mm** on all four sides
- Case wall thickness: **7 mm**
- Electronics bay depth: **17.5 mm**
- Compartment height: **27.5 mm**
- Switch/LED facing: north

These can be changed with the shared options, for example:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- \
  --all 3 --wall 5 --compartment-depth 25 --compartment-height 20 \
  --led-facing south --output build/kb60_custom.stl
```

See [AGENTS.md](AGENTS.md) for the full option reference, mesh conventions, and
development rules.

## Screenshots

See [Screenshots and generation commands](SCREENSHOTS.md) for one image per
keyboard and the exact commands used to generate and render them. The rendered
previews are copied into the `screenshots/` directory for the documentation.

## Repository layout

```text
keyboards/
  layout.py          pure layout model
  mesh.py            bmesh primitives, booleans, validation
  hardware.py        MX switch, hotswap and LED cutouts
  stabilizer.py      custom stabilizer geometry
  plate.py           flat top plate
  case.py            case and USB-C opening
  cover.py           case cover
  generate.py        keyboard orchestration
  kb60/main.py       60% entry point
  kb75/main.py       75% entry point
  kb100/main.py      100% entry point
  kbNumpad/main.py   numpad entry point
  stabilizers/main.py
  render_preview.py
```

Generated STLs and working previews under `build/` are build artifacts. The
preview copies under `screenshots/` are documentation images used by the
screenshot page.
