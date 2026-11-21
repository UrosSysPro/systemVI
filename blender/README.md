# Keyboard Generator

Headless Blender scripts that generate 3D-printable keyboard housings and
stabilizers. Every keyboard is built procedurally and exported as binary STL
files under `build/`; there is no GUI step and no `.blend` file to maintain.

Each keyboard produces four part types:

- **Top plate** — MX switch pockets, hotswap-socket pockets, LED pockets/light
  holes, stabilizer post holes/wire channels, and 4 mm lower side tabs with M2
  clearance holes. The tabs are clamped by the two case parts.
- **Case bottom** — inset bottom tray with the 1.5 mm wire recess, short
  inter-key support ribs, an upstanding perimeter lip and M2 counterbores. Its
  underside is the only visible face.
- **Case edges** — full-height outer edge ring with an electronics bay, USB-C
  opening with a 45° beveled recess and internal glue shelf. From outside it
  looks exactly like the old one-piece case. Inside it has a lower recess for
  the bottom tray and plate tabs, with vertical M2 heatset inserts above each
  tab. A small 45-degree shelf under each magnet in the bay holds the cover.
- **Cover** — thin L-shaped lid for the electronics bay, with local magnet
  bosses and pockets on its underside. Only the magnet locations are thickened.

When the case is wider than **250 mm** the edge ring is automatically split into
four dovetailed pieces (see [Split case edge ring](#split-case-edge-ring)); the
top plate, bottom tray and cover are still exported whole.

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

Run from the repository root. Each command writes the top plate, both case
parts and the cover into its own `build/<name>/` tree:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py
blender --background --python-exit-code 1 --python keyboards/kb75/main.py
blender --background --python-exit-code 1 --python keyboards/kb100/main.py
blender --background --python-exit-code 1 --python keyboards/kbNumpad/main.py
```

The outputs are named:

```text
build/<name>/top_plate/top_plate.stl
build/<name>/case/case_bottom.stl
build/<name>/case/case_edges.stl
build/<name>/cover/cover.stl
```

On a case wider than 250 mm the `case/case_edges.stl` output is replaced by
four dovetailed pieces in the same `case/` directory:

```text
build/<name>/case/case_edges_west.stl
build/<name>/case/case_edges_east.stl
build/<name>/case/case_edges_north.stl
build/<name>/case/case_edges_south.stl
```

Assembly: fit 6 × 3 × 3 magnets in the case shelves and cover bosses — two per
side edge with their long sides along the edge, plus an adaptive evenly spaced
row along the top edge. Install one M2 heatset insert above each edge-ring tab
location. When the edge ring is split, place the two middle rails first and lower
the side pieces onto them from above; the slots are open at the bottom and the
caps stop the rails lifting. Glue the joints. Lower the top plate into the edge
ring from below, then
fit the bottom tray from below: its perimeter lip enters the edge-ring recess and
its counterbores line up with the plate tabs. Screw M2 × 10 socket-head screws up
from underneath into the inserts; the bottom tray and edge ring bottom faces
are flush. Route wires in the 1.5 mm recess between the support ribs.

The case split also leaves the bottom tray’s vertical edge hidden inside the
edge ring, so only the underside of the bottom part is visible from outside.

## Split top plate

The top plate has geometry on both sides, so it can be exported as two
horizontal halves with one flat side each:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- --split-plate
```

This replaces the normal top plate with two files in the `top_plate/`
directory:

```text
build/<name>/top_plate/top_plate_top_half.stl
build/<name>/top_plate/top_plate_bottom_half.stl
```

The cut is at z = 4.0 mm, through the 1.5 mm switch/socket web and exactly at
the top of the fastening tabs. The top half has the switch pockets; the bottom
half has the hotswap and LED pockets plus all fastening tabs. Both halves must
pass mesh validation before either is exported. The split case and cover are
still generated normally.

## Split case edge ring

A 250 mm print bed cannot hold the edge ring of the 60%, 75% or 100% boards
(their cases are about 304, 323 and 447 mm wide). When the case is wider than
**250 mm**, the generator automatically cuts the edge ring along X at one
quarter and three quarters of the case width — the 1:2:1 sections — and exports
four pieces into `build/<name>/case/`:

```text
build/<name>/case/case_edges_west.stl
build/<name>/case/case_edges_east.stl
build/<name>/case/case_edges_north.stl
build/<name>/case/case_edges_south.stl
```

The two cuts cross the north and south walls, so the closed ring becomes a west
U (west wall plus the west wall stubs), the middle north rail, the middle south
rail and an east U. On the 100% board the widest piece is about 232 mm.

Each seam has a dovetail inside the wall: the rails carry the male dovetails and
the side pieces the matching slots. A dovetail is 4 mm deep, 2.5 mm wide at the
rail end and 3.7 mm wide at the tip, with 0.2 mm total clearance and a 0.2 mm
seam gap. The male dovetail stops **2.5 mm below** the wall top. On the south
wall it starts 1.5 mm above the plate-tab shelf; on the north wall it runs down
to the case bottom (the bottom tray does not reach the bay wall) and fills the
slot there. The slot is cut right through the bottom of the side piece and stops
just above the plug top, so it is **open at the bottom and capped at the top**.
The side pieces are lowered onto the rails from above: the plug enters the slot
from below and the cap then stops the rail lifting out. Glue the joints.

Because the slot is open at the bottom, the rails are retained downward by the
glue, the plate or the tray rather than by a slot floor. Print note: the south
dovetail undercuts the case's lower recess by about 3.6 mm; support that
underside or orient the south rail so it is not a downward overhang. The north
dovetail reaches the case bottom. The plate, bottom tray and cover are not split
by this feature.

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

The preview uses the default plate, split case and magnet parameters.

For any single part from any angle (including below the part), use the part
renderer:

```sh
blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
  --keyboard kb75 --part case_edges --azimuth 35 --elevation 25
```

`--azimuth` orbits around the part (0 = front/south, 90 = east) and
`--elevation` looks from above or below.
`--part plate|case|case_bottom|case_edges|cover|slider|assembly` selects what to
build; `case` renders both case parts. `--stl PATH` renders an exported STL
instead.

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
- Fastening: **M2 × 10** socket-head screws and **3.0 mm OD** heatset inserts;
  adaptive tabs on both sides and the south edge, 4 mm high, 3.6 mm deep,
  12 mm wide
- Case split: bottom tray inset behind the edge ring; both undersides flush
- Wide cases: edge ring auto-split above **250 mm** case width at the 1:2:1
  cuts, with 4 dovetails (4 mm deep, 2.5/3.7 mm wide, 0.2 mm clearance)

These can be changed with the shared options, for example:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- \
  --all 3 --wall 7 --compartment-depth 25 --compartment-height 20 \
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
  mounting.py        plate tabs, cover magnets, wire ribs
  stabilizer.py      custom stabilizer geometry
  plate.py           flat top plate
  case.py            split case parts, USB-C opening, magnet shelves
  case_split.py      wide edge-ring split with dovetail joints
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
