# AGENTS.md

Instructions for this repository: a collection of headless Blender scripts that
generate 3D-printable keyboard parts. No GUI step, no `.blend` file, no manual
modelling. Everything is built procedurally and exported as STL.

## Current state

Implemented keyboards:

| Entry point | Layout | Parts |
|---|---|---|
| `keyboards/kbNumpad/main.py` | 17-key numpad | plate, case, cover |
| `keyboards/kb60/main.py` | ANSI 61-key 60% | plate, case, cover |
| `keyboards/kb75/main.py` | 83-key 75% | plate, case, cover |
| `keyboards/kb100/main.py` | ANSI 104-key full-size | plate, case, cover |

Placeholders:

- `keyboards/corne/main.py` — standard 3x6 + 3 thumb Corne, not implemented.
- `keyboards/dactyl/main.py` — one Dactyl variant, not implemented.

Both placeholders print a clear message and exit with status 1. They do not write
files.

`cube/main.py` is a legacy standalone example. It is intentionally independent of
the keyboard suite.

## Golden rules

1. **Build and export.** Every model entry point builds geometry and writes STL
   files. Nothing else is required of a model script.
2. **Headless only.** Run through
   `blender --background --python-exit-code 1 --python <script>.py`. A script
   must not depend on a viewport, active window, GUI or user input. Assume no
   display.
3. **One entry point per keyboard layout.** Each keyboard lives in
   `keyboards/<name>/main.py`. Shared logic belongs in the modules directly under
   `keyboards/`, not in a layout directory.
4. **Build output is not source.** STLs and working previews are generated
   artifacts. Write STLs only under `build/`; keep documentation copies in
   `screenshots/`.
5. **Reproducible.** The same script and Blender version must produce the same
   model. Do not add randomness unless it is explicitly seeded.
6. **Millimetres.** One Blender unit equals one millimetre in the exported STL.
7. **Fail loudly.** Verify the output exists and is non-empty. A bad mesh must
   raise, not just print a warning.
8. **No third-party Python dependencies.** Use Blender's bundled `bpy`, `bmesh`,
   `mathutils` and the standard library only.

## Repository layout

```text
blender/
├── AGENTS.md
├── README.md
├── SCREENSHOTS.md
├── screenshots/                 # documentation preview images
├── .gitignore
├── build/                       # generated STLs/PNGs, never source
├── cube/                        # legacy standalone example
│   └── main.py
├── keyboards/
│   ├── layout.py                # pure layout model; no bpy
│   ├── mesh.py                  # bmesh primitives, booleans, validation
│   ├── hardware.py              # MX switch, hotswap, LED cutouts
│   ├── stabilizer.py            # custom 2 mm-wire stabilizer cutouts + slider
│   ├── plate.py                 # flat top plate builder
│   ├── case.py                  # case, bay, USB-C opening, glue shelf
│   ├── cover.py                 # L-shaped/flat cover
│   ├── export.py                # scene reset + atomic STL export
│   ├── cli.py                   # shared argparse and output paths
│   ├── generate.py              # KeyboardSpec + generate_keyboard
│   ├── keyboard_parts.py        # deprecated compatibility facade
│   ├── keyboard_case.py         # deprecated compatibility facade
│   ├── render_preview.py        # optional assembled preview renderer
│   ├── kbNumpad/main.py
│   ├── kb60/main.py
│   ├── kb75/main.py
│   ├── kb100/main.py
│   ├── corne/main.py            # placeholder
│   ├── dactyl/main.py           # placeholder
│   └── stabilizers/main.py      # standalone slider generator
└── .dsh/                        # harness tooling, ignored; not model code
```

## Coordinates and units

- `1u = 19.05 mm` (`layout.PITCH`).
- `+Y` is north, the function/top-row side. Rows go south as Y decreases.
- The top plate is centered on the origin; the case and cover use the same
  centered coordinates so the parts align.
- Top plate local Z is `0 .. 9.5 mm`. In the assembly the plate sits on the case
  floor: `z = 3 .. 12.5 mm`.
- The case rim is at 15 mm. The default compartment and cover top are at
  **27.5 mm**; the cover front wall is 15 mm tall.
- The default case uses 7 mm walls and a 17.5 mm-deep electronics bay.
- `--led-facing south` adds 180 degrees to every key's cutout rotation. It flips
  the whole switch footprint, not only the LED. North and south plates are not
  interchangeable; generate the matching variant.

## Generation pipeline

```text
layout data
   │
   ├─ keys_from_rows() / keys_from_units() ──► Key[]
   │
   ├─ layout_bounds() ──► plate/case rectangle
   │
   ├─ build_top_plate()  ──► build/top_plate.stl
   ├─ build_case()       ──► build/case.stl
   └─ build_cover()      ──► build/cover.stl
```

`generate_keyboard(spec)`:

1. validates case metrics and compartment values before touching the scene,
2. resets the scene once,
3. builds all three parts,
4. runs strict mesh validation on each,
5. exports all three with atomic replacement.

### Dependency direction

- `layout.py` is pure Python. It must not import `bpy` or `bmesh`.
- `mesh.py` owns low-level bmesh primitives, booleans and validation.
- `hardware.py` owns shared MX/hotswap/LED cutout dimensions and cutters.
- `stabilizer.py` owns the custom stabilizer dimensions, plate cutters and slider.
- `plate.py` imports layout, hardware, stabilizer and mesh.
- `case.py` imports layout, hardware and mesh.
- `cover.py` imports case and mesh.
- `export.py`, `cli.py` and `generate.py` sit above the builders.
- Keyboard `main.py` files contain only the layout data, path setup, CLI call and
  `KeyboardSpec` construction.

`keyboard_parts.py` and `keyboard_case.py` are compatibility shims for older
imports. Do not add new features there; use the focused modules.

## Running

Run from the repository root:

```sh
blender --background --python-exit-code 1 --python keyboards/kb75/main.py
```

Use `--python-exit-code 1`; without it, Blender may exit 0 even when a Python
exception occurred.

Arguments go after Blender's `--` separator:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- \
  --led-facing south --horizontal 6 --vertical 4 --output build/kb60_wide.stl
```

### Shared CLI options

| Option | Meaning |
|---|---|
| `--led-facing north\|south` | Switch/LED orientation. |
| `--all MM` | All four plate margins. |
| `--horizontal MM` | Left and right margins. |
| `--vertical MM` | Top and bottom margins. |
| `--padding-left/right/top/bottom MM` | Individual sides; highest precedence. |
| `--compartment-height MM` | Raised bay height (default 27.5 mm). |
| `--compartment-depth MM` | Internal north/south depth of the bay (default 17.5 mm). |
| `--wall MM` | Case outer wall thickness (default 7 mm). |
| `--compartment-ramp MM` | Horizontal run of the raised-compartment slope; 0 gives a vertical step. |
| `--split-plate` | Export the top plate as two horizontal halves instead of one STL; case and cover are unchanged. |
| `--output PATH` | Base STL path; case/cover reuse the stem with `_case`/`_cover`; split mode adds `_top_half`/`_bottom_half`. |

Padding precedence is `--all`, then `--horizontal`/`--vertical`, then the
individual sides.

## Outputs

Default outputs:

```text
build/<name>_top_plate.stl
build/<name>_case.stl
build/<name>_cover.stl
```

Examples: `build/kb75_top_plate.stl`, `build/kb100_case.stl`,
`build/kbNumpad_cover.stl`.

`--output` names the plate; for example:

```sh
--output build/kb60_south.stl
```

writes `build/kb60_south.stl`, `build/kb60_south_case.stl` and
`build/kb60_south_cover.stl`.

With `--split-plate`, the normal top plate is replaced by:

```text
build/<name>_top_plate_top_half.stl
build/<name>_top_plate_bottom_half.stl
```

The split is through the middle of the 1.5 mm switch/socket web at z = 3.75 mm.
The top half contains the switch pockets; the bottom half contains the
hotswap/LED pockets. Both halves are validated strictly before export. The case
and cover are still exported unchanged.

The standalone stabilizer generator writes:

```text
build/stabilizer_slider.stl
```

Preview renders write `build/<keyboard>_preview.png`. Generated STLs and working
previews under `build/` are build artifacts; documentation copies live in
`screenshots/`.

## Layout model

`layout.py` provides:

- `PlateParams` — four margins, `led_facing`, `facing_angle()`.
- `Key` — centre in mm, cap width/height, rotation; `bounds()` handles rotated
  key footprints.
- `Rect` — axis-aligned rectangle used for plate/case metrics.
- `keys_from_rows(rows)` — row widths in units; `None` advances one unit.
- `keys_from_units(points)` — explicit centres; optional width, height and
  rotation in degrees.
- `layout_bounds(keys, params)` — rotated key AABB plus the four margins.

Flat keyboards can also build clusters directly with `Key`, as `kb100/main.py`
does for its main block, navigation cluster and numpad.

## Shared hardware cutouts

`hardware.py` contains the common MX/hotswap/LED stack.

### Switch pocket

- Stepped 5-pin MX pocket.
- `14 x 14 mm` band at the surface.
- `15 x 14 mm` clip relief 1 mm below the surface.
- Pocket depth 5 mm.
- 4.2 mm centre-post hole, two 3.2 mm pin holes and two 2.0 mm leg holes through
  the plate floor.

### Hotswap socket

Current known-good profile:

- Body pocket: **12 x 9 mm**, centred at `(-0.635, -3.81)` from the switch
  centre.
- Pin/contact holes: `(3.81, -2.54)` and `(-2.54, -5.08)`, diameter 3.2 mm.
  This is the X-mirrored pad pattern.
- One **2 x 2 mm side tab** clearance on the higher pin's side:
  - +X/right side,
  - Y = `-2.54 mm`,
  - 2 mm outward from the body edge,
  - 0.2 mm overlap into the body pocket.
- The lower pin's side is covered by the original 12 x 9 body pocket.
- Socket pocket depth: 3 mm.

The physical hotswap pad body was measured at 11 x 7 mm. A literal 11 x 7 plate
pocket creates non-manifold slivers where the switch-leg holes break the pocket
corner. The current 12 x 9 envelope plus one tab is the validated compromise.
Do not blindly shrink it; if a tighter fit is required, add local leg relief and
revalidate all four layouts.

**Cut order matters.** In `plate.py` the socket body is cut before the leg/pin
holes, and the side tab is cut **last**, after the through-holes. Changing that
order can reintroduce non-manifold edges on the 100% plate.

### RGB LED

- `5.2 x 5.2 x 1.7 mm` bottom pocket for a WS2812B 5050 package.
- `3.6 mm` light hole through the plate.
- LED offset `+5.08 mm` in Y (north) before facing rotation.

## Stabilizers

Custom stabilizers use **2 mm solid copper wire**. The printed slider is
generated by `keyboards/stabilizers/main.py`.

### Coverage and orientation

- A key is stabilized when its width is at least 2u.
- A 1u-wide, 2u-tall key gets a **vertical** stabilizer; the same slider is
  rotated 90 degrees.
- Current covered keys:
  - 60%: 2u Backspace, 2.25u LShift/Enter, 2.75u RShift, 6.25u Space.
  - 75%: 2u Backspace, 2.25u LShift/Enter, 6u Space.
  - 100%: the above plus 2u numpad `0`; numpad `+`/Enter get vertical stabs.
  - Numpad: 2u `0` horizontal; `+`/Enter vertical.

### Stabilizer spacing and wire

- 2u, 2.25u, 2.75u: centre offset ±11.93 mm (23.86 mm apart).
- 3u and wider: offset = `(units - 1) * 19.05 / 2`.
- 6u span ~95.25 mm; 6.25u span ~100.0 mm; 7u span ~114.3 mm.
- Wire: 2.0 mm diameter, 9.5 mm horizontal legs, 1.7 mm bend radius, two 90
  degree bends, cut length `2 * 9.5 + span + pi * 1.7` plus trim allowance.
- The generator prints the exact cut lengths for standard widths.

### Plate cutouts

- Two square post holes per stabilized key: **5.4 x 5.4 mm** through the plate.
- A U-shaped wire channel in the plate bottom:
  - width 3.8 mm,
  - depth 7.4 mm from the plate bottom,
  - wire centre at Z = 6.2 mm,
  - horizontal span offset 9.5 mm north of the switch (or east for vertical
    keys),
  - routed to clear the hotswap/LED features.
- The case floor closes the channel and traps the wire.

### Slider

- One symmetric STL: `build/stabilizer_slider.stl`.
- Print **two per stabilized key**.
- Envelope: 5.0 x 5.0 x 17.1 mm.
- 4.0 x 1.1 mm MX cross on top.
- 1.2 mm bottom floor with a slanted 2.4 mm wire hole feeding an internal
  capture slot.
- 4.0 mm post travel target; total slider height 17.1 mm (10.5 mm body plus a
  6.6 mm MX cross).
- For a default north-facing vertical key, rotate the slider 90 degrees
  clockwise so its slot opens east. For a south-facing vertical key the wire
  path mirrors west; rotate the slider the other way.

Run:

```sh
blender --background --python-exit-code 1 --python keyboards/stabilizers/main.py
```

or print one width's wire data:

```sh
blender --background --python-exit-code 1 --python keyboards/stabilizers/main.py -- --width 6.25
```

## Case, cover and USB-C

`case.py` builds a hollow tray with rounded corners, 45 degree rim chamfers and a
north electronics bay.

Key dimensions:

- wall 7 mm (default; configurable with `--wall`), floor 3 mm, chamfer 1.5 mm, corner radius 7 mm,
- plate pocket clearance 0.25 mm per side,
- bay depth 17.5 mm (default; configurable with `--compartment-depth`),
- lid thickness 2.5 mm, shelf 1.5 mm, lid clearance 0.2 mm,
- default compartment/cover top 27.5 mm; rim 15 mm; plate top 12.5 mm,
- cover front wall 2.5 mm thick and 15 mm tall.

When the compartment is raised, the outer shell gets its normal chamfer at the
raised height, then the ramp cutter lowers the key-side section to the 15 mm rim.
That newly exposed lower rim is chamfered **after** all cuts in
`_bevel_lower_rim`, so the key-side rim keeps the same 1.5 mm top bevel as the
raised compartment. This is a targeted outer-rim edge bevel, not a global bevel
of the case.

### USB-C opening

The USB-C cutout is on the north wall, 12 mm from the bay's inner west wall:

- through-opening: 9.2 x 3.4 mm, 1.0 mm corner radius,
- flat seat ring: 1.5 mm wide, outer edge 12.2 x 6.4 mm, radius 2.5 mm,
- 45 degree bevel: 2.0 mm deep,
- outer opening: 16.2 x 10.4 mm, radius 4.5 mm,
- internal glue shelf: 16.2 mm wide, 6 mm deep, 2.5 mm thick, embedded 0.5 mm
  into the inner wall.

The shelf is added after the bay is cut and before the USB cutters, with a
0.2 mm top overlap that the through-opening cutter shaves flush. It is attached
to the inner north wall, not floating.

The shelf is a 6 mm internal cantilever; it may need a small support depending on
the printer's overhang capability.

## Preview renderer

`keyboards/render_preview.py` builds the selected keyboard and renders an
assembled image:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- \
  --keyboard kb75
```

Options:

- `--keyboard kbNumpad|kb60|kb75|kb100`
- `--output build/foo.png`
- `--samples N`
- `--width PX`, `--height PX`

Palette:

- case `#66D9EF` cyan,
- cover `#F92672` pink,
- top plate `#E6DB74` yellow,
- background `#272822`.

The previewer is not a model generator and does not write STLs. It uses the
default plate/case parameters, so it matches the default generated parts.

## Adding a new flat keyboard

1. Create `keyboards/<name>/main.py`.
2. Add a module docstring with the layout and output names.
3. Set up paths:
   - `SCRIPT_DIR`
   - `KEYBOARDS_DIR = dirname(SCRIPT_DIR)`
   - `PROJECT_DIR = dirname(KEYBOARDS_DIR)`
   - `sys.path.insert(0, KEYBOARDS_DIR)`
4. Define the layout with `layout.keys_from_rows`, `layout.keys_from_units`, or
   explicit `layout.Key` objects.
5. Build paths with `cli.default_paths(PROJECT_DIR, NAME)` and
   `cli.output_paths(...)`.
6. Create a `generate.KeyboardSpec` and call `generate.generate_keyboard(spec)`.
7. Do not duplicate plate/case/cover code in the layout file.

Example skeleton:

```python
NAME = "kbXX"
KEYS = layout.keys_from_rows(ROWS)

args = cli.parse_args(argv, description=f"{NAME} ({len(KEYS)} keys)")
params = cli.params_from_args(args)
plate, case_path, cover_path = cli.output_paths(
    args.output, *cli.default_paths(PROJECT_DIR, NAME)
)
spec = generate.KeyboardSpec(
    name=NAME,
    keys=KEYS,
    plate_path=plate,
    case_path=case_path,
    cover_path=cover_path,
    params=params,
    compartment_height=args.compartment_height,
    compartment_ramp=args.compartment_ramp,
    wall=args.wall,
    compartment_depth=args.compartment_depth,
    split_plate=args.split_plate,
)
generate.generate_keyboard(spec)
```

### Definition of done for a new keyboard

- `blender --background --python-exit-code 1 --python keyboards/<name>/main.py`
  exits 0.
- Default mode creates three STLs under `build/`; split mode creates the two
  plate halves plus the case and cover.
- Every part passes strict mesh validation (manifold, no zero-area faces).
- Dimensions are plausible and the parts align.
- North and south variants both generate if the layout is intended to support
  both facings.
- Preview renders if a preview is requested.

## Adding or changing hardware

- Put shared MX/hotswap/LED dimensions and cutters in `hardware.py`.
- Put stabilizer dimensions and cutters in `stabilizer.py`.
- Keep all dimensions in millimetres at the top of the module.
- Update `plate.py` only for cutout ordering/assembly, not hardware constants.
- Re-run all implemented layouts after any hardware change. The 100% layout is
  the most likely to expose boolean/topology problems.

## Validation

There is currently no formal test suite. Generation is the test, and the mesh
validator is strict:

- `mesh.check_mesh()` fails on non-manifold edges, zero-area faces or an empty
  mesh.
- `generate_keyboard()` validates dimensions before building.
- `export_stl()` verifies the file and uses atomic replacement.

Recommended validation matrix after shared changes:

```sh
for k in kbNumpad kb60 kb75 kb100; do
  blender --background --python-exit-code 1 --python keyboards/$k/main.py
done
```

Also generate the south-facing variants when hardware/cutout orientation changes:

```sh
blender --background --python-exit-code 1 --python keyboards/kb100/main.py -- \
  --led-facing south --output build/kb100_south.stl
```

Run the stabilizer generator after changes to stabilizer constants:

```sh
blender --background --python-exit-code 1 --python keyboards/stabilizers/main.py
```

## Known constraints and pitfalls

- **Hotswap pocket size:** the current validated pocket is 12 x 9 mm plus one
  higher-pin tab. A literal 11 x 7 mm pocket produced non-manifold slivers at the
  switch-leg openings.
- **Hotswap tab order:** the tab must be cut after the through-holes. The socket
  body is cut before them.
- **South facing:** all switch/hotswap/stabilizer cutouts rotate 180 degrees.
  Use the matching `--led-facing south` plates; do not mix north and south.
- **Vertical stabilizers:** default wire direction is east; the slider rotation
  differs for south-facing vertical keys.
- **Case shelf:** the USB glue shelf is an internal cantilever and may need a
  support on some printers.
- **100% size:** the single-piece 100% case is about 447 mm wide; confirm the
  printer bed size before printing.
- **Compatibility shims:** do not add features to `keyboard_parts.py` or
  `keyboard_case.py`.
- **Build PNGs:** STLs and working previews under `build/` are generated
  artifacts. Documentation previews in `screenshots/` are intentionally kept
  for README/SCREENSHOTS.

## Future Corne and Dactyl

- **Corne:** standard 3x6 + 3 thumb per half, split. The flat plate builder and
  layout model can be reused per half. The split case/cover need a dedicated
  builder; do not force them into the single-case `case_metrics`.
- **Dactyl:** curved key wells. A new curved builder is required; the shared
  switch/LED/hotswap dimensions and mesh validation from `hardware.py`/`mesh.py`
  should be reused with 3D transforms. The current flat cup/plate builder is not
  sufficient.
- Keep placeholders until a real generator exists. A placeholder must not write
  STLs and should exit 1.

## Git hygiene

- `.gitignore` ignores `*.stl`, `*.STL`, `.dsh/`; `keyboards/.gitignore` ignores
  `__pycache__/`.
- Commit sources: `AGENTS.md`, `README.md`, `SCREENSHOTS.md`, `.gitignore`,
  shared modules, layout `main.py` files, preview and stabilizer scripts, and the
  documentation previews in `screenshots/`.
- Never commit generated STLs, working previews under `build/`, Blender backups
  (`*.blend1`) or `__pycache__`.
- Keep generated files under `build/` so source directories stay clean.
