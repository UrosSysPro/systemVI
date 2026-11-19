# Keyboard Generator

Headless Blender scripts that generate 3D-printable keyboard housings and
stabilizers. Every keyboard is built procedurally and exported as binary STL
files under `build/`; there is no GUI step and no `.blend` file to maintain.

Each keyboard produces three printed parts:

- **Top plate** — MX switch pockets, hotswap-socket pockets, LED pockets/light
  holes, and stabilizer post holes/wire channels. It has no fastening holes; it
  sits in the case pocket on the support ribs.
- **Case** — hollow tray with an electronics bay, USB-C opening with a 45°
  beveled recess and internal glue shelf. The plate-pocket floor is recessed
  1.5 mm for wires and has short ribs between the keycaps; a small 45-degree
  shelf under each magnet in the bay holds the cover.
- **Cover** — thin L-shaped lid for the electronics bay, with local magnet
  bosses and pockets on its underside. Only the magnet locations are thickened.

Custom stabilizer sliders are generated separately for use with 2 mm solid
copper wire.

## Implemented keyboards

- 60% — ANSI 61-key
- 75% — 83-key
- 100% — ANSI 104-key full-size
- Numpad — 17-key

Corne and Dactyl are planned; their entry points are placeholders.

## Requirements

- Blender 4.2 or newer; tested with Blender 5.3.
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

Assembly: drop the plate into its pocket (it is not fastened for now), fit
6 × 3 × 3 magnets in the case shelves and cover bosses — two per side edge with
their long sides along the edge, plus an adaptive evenly spaced row along the
top edge — and route wires in the 1.5 mm recess between the support ribs.

## Split top plate

The top plate has geometry on both sides, so it can be exported as two
horizontal halves with one flat side each:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- --split-plate
```

This replaces the normal top plate with:

```text
build/<name>_top_plate_top_half.stl
build/<name>_top_plate_bottom_half.stl
```

The cut is through the middle of the 1.5 mm switch/socket web at z = 3.75 mm.
The top half has the switch pockets; the bottom half has the hotswap and LED
pockets. Both halves must pass mesh validation before either is exported. The
case and cover are still generated normally.

## Stabilizer slider

Generate the custom slider with:

```sh
blender --background --python-exit-code 1 --python keyboards/stabilizers/main.py
```

Print two sliders per stabilized key. The script also prints the required
copper wire cut lengths.

## Preview

Render an assembled keyboard with:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb60
```

The preview uses the default plate, case and magnet parameters.

For any single part from any angle (including below the part), use the part
renderer:

```sh
blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
  --keyboard kb75 --part case --azimuth 35 --elevation 25
```

`--azimuth` orbits around the part (0 = front/south, 90 = east) and
`--elevation` looks from above or below. `--part plate|case|cover|slider|assembly`
selects what to build; `--stl PATH` renders an exported STL instead.

## Current defaults

- Top-plate padding: **2 mm** on all four sides
- Case wall thickness: **7 mm**
- Electronics bay depth: **17.5 mm**
- Compartment height: **27.5 mm**
- Switch/LED facing: north
- Cover: **2.5 mm** lid with **1.5 mm** local bosses around the magnets
- Magnets: **6 × 3 × 3**; two per side edge with their long sides along the
  edge when they fit, otherwise one centered per side, plus an adaptive evenly
  spaced top-edge row (default target spacing **40 mm**)
- Wire recess: **1.5 mm** below the plate, with 3 × 10 mm ribs in the keycap gaps
- Top plate: not fastened for now

These can be changed with the shared options, for example:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- \
  --all 3 --wall 5 --compartment-depth 25 --compartment-height 20 \
  --led-facing south --output build/kb60_custom.stl
```

Magnet and wire sizes are parameters too:

```sh
blender --background --python-exit-code 1 --python keyboards/kb75/main.py -- \
  --magnet-width 6 --magnet-length 3 --magnet-thickness 3 \
  --magnet-spacing 30 --wire-gap 1.5
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
  mounting.py        cover magnets and inter-key wire ribs
  stabilizer.py      custom stabilizer geometry
  plate.py           flat top plate
  case.py            case, USB-C opening, wire recess, magnet shelves
  cover.py           cover with local magnet bosses
  export.py          scene reset and atomic STL export
  cli.py             shared command-line options
  generate.py        keyboard orchestration
  keyboard_parts.py  deprecated compatibility facade
  keyboard_case.py   deprecated compatibility facade
  kb60/main.py       60% entry point
  kb75/main.py       75% entry point
  kb100/main.py      100% entry point
  kbNumpad/main.py   numpad entry point
  corne/main.py      placeholder
  dactyl/main.py     placeholder
  stabilizers/main.py
  render_preview.py
  render_part.py     any-part, any-angle preview
tests/               pure and Blender-side tests
```

Generated STLs and working previews under `build/` are build artifacts. The
preview copies under `screenshots/` are documentation images used by the
screenshot page.

Tests live in `tests/`; see [tests/README.md](tests/README.md) for the pure and
Blender test commands.
