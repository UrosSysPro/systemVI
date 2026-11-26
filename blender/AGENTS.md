# AGENTS.md

Instructions for this repository: a collection of headless Blender scripts that
generate 3D-printable keyboard parts. No GUI step, no `.blend` file, no manual
modelling. Everything is built procedurally and exported as STL.

## Current state

Implemented keyboards:

| Entry point | Layout | Parts |
|---|---|---|
| `keyboards/kbNumpad/main.py` | 17-key numpad | plate, case bottom, case edges, cover, tilt bar |
| `keyboards/kb60/main.py` | ANSI 61-key 60% | plate, case bottom, case edges, cover, tilt bar |
| `keyboards/kb75/main.py` | 83-key 75% | plate, case bottom, case edges, cover, tilt bar |
| `keyboards/kb100/main.py` | ANSI 104-key full-size | plate, case bottom, case edges, cover, tilt bar |

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
│   ├── mounting.py              # plate tabs, cover magnets, tilt bar, wire ribs
│   ├── stabilizer.py            # custom 2 mm-wire stabilizer cutouts + slider
│   ├── plate.py                 # flat top plate + lower locating tabs
│   ├── case.py                  # split case bottom/edges, bay, USB-C, magnets
│   ├── case_split.py            # wide edge-ring split + dovetail joints
│   ├── cover.py                 # L-shaped/flat cover with bosses + front-top chamfer
│   ├── tilt.py                  # removable rear tilt bar + locating pins
│   ├── export.py                # scene reset + atomic STL export
│   ├── cli.py                   # shared argparse and output paths
│   ├── generate.py              # KeyboardSpec + generate_keyboard
│   ├── keyboard_parts.py        # deprecated compatibility facade
│   ├── keyboard_case.py         # deprecated compatibility facade
│   ├── render_preview.py        # assembled preview renderer
│   ├── render_part.py           # any-part, any-angle preview renderer
│   ├── render_exploded.py       # exploded-view renderer
│   ├── render_dovetail.py       # male/female dovetail close-ups
│   ├── kbNumpad/main.py
│   ├── kb60/main.py
│   ├── kb75/main.py
│   ├── kb100/main.py
│   ├── corne/main.py            # placeholder
│   ├── dactyl/main.py           # placeholder
│   └── stabilizers/main.py      # standalone slider generator
├── tests/                       # pure and Blender-side tests
└── .dsh/                        # harness tooling, ignored; not model code
```

## Coordinates and units

- `1u = 19.05 mm` (`layout.PITCH`).
- `+Y` is north, the function/top-row side. Rows go south as Y decreases.
- The top plate is centered on the origin; the case and cover use the same
  centered coordinates so the parts align.
- Top plate local Z is `0 .. 9.5 mm`. In the assembly the plate sits on the
  inter-key support ribs at `z = 4 .. 13.5 mm`. The rest of the pocket floor is
  recessed 1.5 mm for wires. The lower 4 mm of the plate carries the locating
  tabs. The plate split is at local z = **3.75 mm**, through the middle of the
  switch/socket web (the middle-pin hole): the bottom half keeps the lower
  3.75 mm of the tabs and the top half gets only their top 0.25 mm edge.
- The case rim is at 15 mm. The default compartment and cover top are at
  **30 mm**; the cover lid is 2.5 mm thick, its front lip is 14 mm tall and the
  exposed front-top edge carries a 0.8 mm x 45-degree chamfer,
  and local 1.5 mm bosses under the magnets hang down to the 45-degree case
  shelves.
- The default case uses 7 mm walls, a **4 mm floor** and a **27.5 mm-deep**
  electronics bay.
- The case is split into a bottom tray and an outer edge ring. The bottom tray
  is inset behind the edge ring, so only its underside is visible; the two
  bottom faces are coplanar. The edge ring has a lower internal recess that
  fits over the tray's perimeter lip and plate tabs, an overhanging shelf
  above the tabs, four screw boxes on the north wall and a triangular ledge on
  the south wall.
- The top plate has adaptive 4 mm-high lower tabs on west, east and south.
  Each tab sits on the bottom tray, under the edge-ring shelf. Two 3 mm wood
  screws enter from below the tray into blind pilot holes in the plate; four
  more go through the tray's north edge into the edge-ring boxes. The north
  edge is free for the bay and cover lip.
- The cover is held by 6 x 3 x 3 magnets: two along each side edge with their
  long sides on Y (or one centered if the span is too short), plus an adaptive
  evenly spaced row along the top edge with long sides on X (target spacing
  40 mm). The edge ring supports each one with its own small 45-degree shelf.
- The rear tilt bar is **80% of the case width**, 17.5 mm deep and 10 mm tall,
  with a 4 mm, 45-degree bevel on both long bottom edges. It sits at the start
  (south edge) of the bay, 1 mm behind the bay front. It is built in printing
  orientation: local z = 0 (bottom) to 10 mm (top), with two 2.5 mm locating
  pins above that. In the assembly the bar is lowered by 10 mm and moved to the
  bay start, so its top face meets the bottom tray at z = 0. The pins are in the
  front row and six fixed 6 x 3 x 3 mm magnets sit behind them. Every magnet
  pocket is an internal cavity closed by a 0.6 mm printed cover; the tray also
  keeps a 0.4 mm bottom skin.
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
   |                        │
   |                        ├─ magnet_plan() / rib_plan() ──► plan
   |                        └─ plate_tab_plan() ──► tab positions
   │
   ├─ build_top_plate()   ──► build/<name>/top_plate/...  (tabs + 2 pilots)
   ├─ build_case_bottom() ──► build/<name>/case/case_bottom.stl
   │                            │
   │                            └─ tilt_plan() ──► blind pin/magnet sockets
   ├─ build_case_edges()  ──► build/<name>/case/case_edges.stl
   │                            │
   │                            └─ split_case_edges() (case width > 250 mm)
   ├─ build_cover()       ──► build/<name>/cover/cover.stl
   └─ build_tilt_bar()    ──► build/<name>/tilt/tilt.stl
```

`generate_keyboard(spec)`:

1. validates case metrics and compartment values before touching the scene,
2. resets the scene once,
3. builds the plate, both case parts, the cover and the tilt bar,
4. splits the edge ring into four dovetailed pieces when the case is wider
   than 250 mm,
5. runs strict mesh validation on each part and piece,
6. exports everything with atomic replacement.

### Dependency direction

- `layout.py` is pure Python. It must not import `bpy` or `bmesh`.
- `mesh.py` owns low-level bmesh primitives, booleans and validation.
- `hardware.py` owns shared MX/hotswap/LED cutout dimensions and cutters.
- `stabilizer.py` owns the custom stabilizer dimensions, plate cutters and slider.
- `mounting.py` owns plate-tab, tilt-bar, magnet and wire dimensions and the
  shared tab/magnet/tilt/rib plans. It is pure Python and imports only layout.
- `plate.py` imports layout, hardware, mesh, mounting and stabilizer.
- `case.py` imports layout, hardware, mesh and mounting; its bottom-tray builder
  cuts the tilt-bar sockets from the shared tilt plan.
- `case_split.py` imports case and mesh; it splits a built wide edge ring and
  owns the dovetail dimensions. `generate.py` calls it above the builders.
- `cover.py` imports case, mesh, hardware and mounting.
- `tilt.py` imports mesh and builds the rear tilt bar.
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
| `--compartment-height MM` | Raised bay height (default 30 mm). |
| `--compartment-depth MM` | Internal north/south depth of the bay (default 27.5 mm). |
| `--wall MM` | Case outer wall thickness (default 7 mm; the split geometry needs at least 7 mm). |
| `--compartment-ramp MM` | Horizontal run of the raised-compartment slope; 0 gives a vertical step. |
| `--split-plate` | Export the top plate as two horizontal halves instead of one STL; the split case and cover are unchanged. |
| `--output PATH` | Explicit flat base STL path; case parts/cover/tilt bar reuse the stem with `_case_bottom`/`_case_edges`/`_cover`/`_tilt`; `--split-plate` adds `_top_half`/`_bottom_half`; a wide case adds `_case_edges_west`/`_east`/`_north`/`_south`. Without it outputs go to `build/<name>/{top_plate,case,cover,tilt}/`. |
| `--magnet-width MM` | Magnet long side, along the edge (default 6.0). |
| `--magnet-length MM` | Magnet short side, across the edge (default 3.0). |
| `--magnet-thickness MM` | Magnet vertical size; sets the local cover boss height (default 3.0). |
| `--magnet-spacing MM` | Target spacing for the adaptive top-edge row (default 40.0). |
| `--wire-gap MM` | Depth of the wire recess under the plate (default 1.5). |

Padding precedence is `--all`, then `--horizontal`/`--vertical`, then the
individual sides.

## Outputs

Default outputs (one directory per keyboard):

```text
build/<name>/top_plate/top_plate.stl
build/<name>/case/case_bottom.stl
build/<name>/case/case_edges.stl
build/<name>/cover/cover.stl
build/<name>/tilt/tilt.stl
```

Examples: `build/kb75/top_plate/top_plate.stl`,
`build/kb100/case/case_bottom.stl`, `build/kb100/case/case_edges_west.stl`,
`build/kbNumpad/case/case_edges.stl`, `build/kbNumpad/cover/cover.stl`,
`build/kbNumpad/tilt/tilt.stl`.

`--output` is an explicit flat override: it names the top plate and the case
parts, cover and tilt bar reuse its stem. For example:

```sh
--output build/kb60_south.stl
```

writes `build/kb60_south.stl`, `build/kb60_south_case_bottom.stl`,
`build/kb60_south_case_edges.stl`, `build/kb60_south_cover.stl` and
`build/kb60_south_tilt.stl`.  Without `--output`, the per-keyboard tree above is
used.

With `--split-plate`, the normal top plate is replaced by two files in the
keyboard's `top_plate/` directory:

```text
build/<name>/top_plate/top_plate_top_half.stl
build/<name>/top_plate/top_plate_bottom_half.stl
```

The split is at z = **3.75 mm**, through the middle of the switch/socket web and
the middle-pin hole. The top half contains the switch pockets and only the top
0.25 mm edge of the locating tabs; the bottom half contains the hotswap/LED
pockets and the lower 3.75 mm of the tabs. Both halves are validated strictly
before export. The split case, cover and tilt bar are still exported unchanged.

When the case is wider than **250 mm** (the 60%, 75% and 100% boards), the
single `case/case_edges.stl` is replaced by four dovetailed pieces in the same
`case/` directory:

```text
build/<name>/case/case_edges_west.stl
build/<name>/case/case_edges_east.stl
build/<name>/case/case_edges_north.stl
build/<name>/case/case_edges_south.stl
```

With `--output build/kb60_south.stl` the pieces become flat
`build/kb60_south_case_edges_west.stl` and so on. The top plate, bottom tray,
cover and tilt bar outputs are unchanged. A 250 mm case is *not* split; the cut
is triggered above 250 mm.

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
- `14.1 x 14.1 mm` band at the surface: the nominal 14 x 14 mm housing plus
  `SWITCH_TOLERANCE = 0.05 mm` per side.
- `14.1 x 15.1 mm` clip relief 1 mm below the surface: the nominal 14 x 15 mm
  relief plus the same 0.05 mm per side; the switch clips are on the north and
  south edges, so the extra clearance is in Y.
- Pocket depth 5 mm.
- 4.2 mm centre-post hole, two 3.2 mm pin holes and two 2.0 mm leg holes through
  the plate floor.

### Hotswap socket

Current known-good profile:

- Body pocket: **14 x 9 mm**. The known-good 12 x 9 mm body is extended by
  **2 mm on the +X side** (the pin nearest the switch centre), so the base body
  still spans the original `-0.635 ± 6 mm` while the extra 2 mm grows the +X
  face to `x = 7.365 mm` (`SOCKET_POCKET_EXTENSION`). The body centre is
  `(0.365, -3.81)` from the switch centre.
- Pin/contact holes: `(3.81, -2.54)` and `(-2.54, -5.08)`, diameter 3.2 mm.
  This is the X-mirrored pad pattern.
- Two **3 x 3 mm** side tabs, one per metal contact, each on its own pin's
  X side and at that pin's Y, so the pair is diagonal rather than symmetric:
  - higher pin `(3.81, -2.54)`: +X/right side, tab centre Y = `-2.54 mm`,
  - lower pin `(-2.54, -5.08)`: -X/left side, tab centre Y = `-5.08 mm`,
  - anchored to the original 12 mm body edge (`SOCKET_POCKET_BASE_LENGTH`), so
    they keep their validated positions and are not pushed out by the
    extension,
  - 3 mm outward from that base body edge,
  - 0.2 mm overlap into the body pocket.
- Socket pocket depth: 3 mm.

The physical hotswap pad body was measured at 11 x 7 mm. A literal 11 x 7 plate
pocket creates non-manifold slivers where the switch-leg holes break the pocket
corner. The current 14 x 9 envelope (12 x 9 base plus the 2 mm +X extension)
plus the two contact tabs is the validated compromise. Do not blindly shrink
it; if a tighter fit is required, add local leg relief and revalidate all four
layouts.

**Cut order matters.** In `plate.py` the socket body is cut before the leg/pin
holes, and both side tabs are cut **last**, after the through-holes. Changing
that order can reintroduce non-manifold edges on the 100% plate.

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

## Case, cover, tilt bar and USB-C

`case.py` builds the case as two parts that share the old outside silhouette: a
bottom tray with an inset perimeter, and a full-height outer edge ring with the
electronics bay.

Key dimensions:

- wall 7 mm (the split needs at least 7 mm; thicker is allowed),
  floor 4 mm, top and bottom chamfer 2.0 mm, corner radius 9 mm,
- plate pocket clearance 0.25 mm per side,
- bay depth 27.5 mm (default; configurable with `--compartment-depth`),
- lid thickness 2.5 mm, shelf ledge 1.5 mm wide, lid clearance 0.2 mm,
- default compartment/cover top 30 mm; rim 15 mm; plate top 13.5 mm,
- cover front wall 2.5 mm thick; front lip 14 mm tall,
- the side lid ledges are notched back by
  `COVER_WALL + RAIL_FRONT_GAP + LID_LEDGE_SETBACK` (**4.7 mm**) at the bay's
  front edge, so the cover's front wall drops past them; the side magnet
  shelves keep the smaller `COVER_WALL + RAIL_FRONT_GAP` (**2.7 mm**) set-back,
- the bay shelf sits 2.5 mm below the compartment top; the 45-degree magnet
  shelves stop 1.5 mm below the shelf so the cover's local bosses meet them.

### Split case and plate location

The bottom tray is inset behind the edge ring, so its vertical edge is hidden
and only its underside is visible.  Both bottom faces are at z = 0.  The edge
ring keeps the current outer wall, chamfer and rounded corner; inside it has a
**straight-sided** lower recess from z = 0 to 8 mm (no undercut, so the tray can
slide in from below).  The 2.0 mm outer bottom chamfer leaves about **0.55 mm**
of outer wall at z = 0.  At z = 8 mm an inward shelf overhangs the tabs.

The bottom tray has:

- a **4 mm bed** with a rounded plan that fits the edge-ring recess with 0.2 mm
  radial clearance,
- an upstanding **4.0 mm perimeter lip** from z = 3.5 to 7.8 mm, interrupted at
  each plate-tab location.  It reaches back to within **0.5 mm** of the plate
  edge, so the plate's lower edge is cradled between the lip segments instead
  of hanging over the lower recess.  It stops 0.2 mm below the edge shelf.  The
  lip enters the edge-ring recess and locates the tray; the tabs sit under the
  shelf and the six screws clamp the stack.

The top plate has adaptive lower tabs: **4 mm high**, **4.5 mm deep**, **12 mm
wide**, on west, east and south.  `plate_tab_plan(width, height)` distributes
them evenly with a 50 mm target spacing, 10 mm preferred corner margin and
6 mm minimum gap.  The tab's outer face lands on the bottom tray's outer face,
leaving 0.2 mm to the edge ring's lower wall, so it closes the gap instead of
dangling; the lip's tab cutout is what lets it through.  In the assembly the tab
sits on the bottom bed and under the edge shelf.  The plate also has two blind
3 mm pilot holes in the webs between three keys.  The north edge has no tabs,
leaving the bay and cover lip free.

### Mounting screws and front key

- Two 3 mm wood screws enter from counterbores in the tray, pass through the
  bed (and the 1.5 mm wire recess) and thread into **7 mm blind pilots** in the
  plate.  The pilots are placed automatically at key-cap junctions, clear of
  every switch, socket, LED and stabilizer cutout.
- Four more screws enter along the north edge through tray counterbores into
  **10 x 6.45 x 8 mm boxes** on the lower north wall.  The boxes locally
  thicken the 2.55 mm lower wall and continue into the wall above the shelf;
  each has a 7.5 mm blind 3 mm pilot.
- The countersinks are **6 mm** across and **1.5 mm** deep, so the 4 mm floor
  leaves 2.5 mm under the head.
- The south inner wall carries an **80%-width triangular ledge** (2 mm deep,
  2 mm high, 0.5 mm embedded in the wall).  Its vertical cross-section is a
  right triangle with a **flat bottom** at z = 2.3, a 45-degree top, and the
  normal wall above and below.  The tray has a matching triangular side dent
  with the same profile and a uniform 0.2 mm print clearance.
  The ledge is interrupted at every south plate tab.

### Rear tilt bar

`tilt.py` builds a removable rear riser; `mounting.tilt_plan()` owns its
dimensions and the matching sockets, so `case.py` and the builder share one
source of truth.

- The bar is **80% of the case width**, **17.5 mm** deep and **10 mm** tall,
  with a **4 mm, 45-degree bevel on both long bottom edges**. It sits at the
  beginning of the bay: its front edge is 1 mm north of `bay.y0`.
- Two wide locating pins stand up from the **front** of the top face: **22% of
  the bar width** long, **4 mm** deep and **2.5 mm** high, at **±25% of the bar
  width** and **4.5 mm forward of the bar centre**.
- Six fixed **6 x 3 x 3 mm** magnets sit in one row **behind the pins**
  (centre **1.0 mm behind the bar centre**), with long sides along X. The
  pockets are `magnet + 0.4 mm` across.
- Both magnet pockets are internal cavities closed by a **0.6 mm printed cover**
  (three 0.2 mm layers). The bar pocket is **3.1 mm** high (0.1 mm clearance)
  and the cover is the top 0.6 mm of the body. The tray pocket is **3.0 mm**
  high with a **0.4 mm bottom skin** and a **0.6 mm cover** in the bay floor.
  Pause at the top of the cavity (bar z = 9.4 mm, tray z = 3.4 mm), insert the
  magnet, and resume.
- The bottom tray also gets blind pin sockets on its underside. Pin sockets are
  **2.6 mm** deep (1.4 mm of floor left). Magnet cavities and pin sockets never
  reach the wire recess or the bay cavity.
- The magnet row uses six (an even count), so no pocket straddles the bottom
  tray's x = 0 split; the pins are also clear of it.
- Test the polarity before closing the covers: the two magnet sets must face
  each other with opposite poles. Glue is optional after the cover is printed.
- The bar is built in print orientation: bottom at z = 0, top at z = 10 mm,
  pins above that, magnet cavities closed by the top cover. In the assembly it
  is lowered by 10 mm and moved to `plan.mount_y`. The 10 mm lift gives about
  5.1 degrees on the five-row cases and 4.4 degrees on the six-row cases.
- The tilt bar is always exported as **one printed part**, as designed; it is
  not split with the case. The numpad and 60% bars (76 mm and 243 mm) fit the
  250 mm print target; the 75% and 100% bars are 259 mm and 358 mm and need a
  bed at least that wide. Document the bed requirement before printing.

### Wide edge-ring split

`case_split.py` splits the finished edge ring when the case is wider than
**250 mm** (`SPLIT_CASE_MAX_WIDTH`); the 60%, 75% and 100% cases qualify and the
numpad stays whole.  The two vertical cuts along X are at ±`width / 4`, giving
the same 1:2:1 sections as the split top plate.  Because the ring is closed,
the cuts produce four pieces:

- **west** and **east** U-shaped side pieces (side wall plus the end stubs of
  the north and south walls),
- the middle **north** rail and middle **south** rail.

Both cuts cross the north and south walls, so every seam gets two dovetails.
The rails carry the male plugs; the side pieces get the matching slots.  The
slot keeps the nominal shape and the plug is deliberately undersized in X and
Y, so the printed pieces slide together instead of binding.

Dovetail dimensions (`case_split.py`):

- female slot: **3.65 mm** deep into the side piece, **2.5 mm** wide at the
  rail's end face and **3.55 mm** at the free tip (about 8.5-degree flanks),
- male plug: generated from the slot profile, offset **0.15 mm per side** and
  **0.15 mm shorter** at the tip.  The undercut (tail wider than the neck) is
  kept for retention,
- inside the wall, **0.4 mm** from the inner face and **0.4 mm** from the outer
  face at the rim.  The band is measured from the wall's inner face and stops
  short of the 2.0 mm top chamfer, so the joint never breaks the outer surface
  or the inner pocket,
- starts **1.5 mm above** the z = 8 mm tab shelf (z = 9.5 mm) on both walls and
  stops **2.5 mm below** the wall top.  The slot is cut right through the
  bottom of the side piece and stops just above the plug top: open at the
  bottom, capped by the side piece's own material at the top.  The side pieces
  are lowered onto the rails from above; the plug enters the slot through its
  bottom opening and the cap stops the rail lifting out,
- **0.15 mm** clearance per side, a **0.15 mm** cap over the plug top and a
  **0.2 mm** seam gap.

The south wall is 15 mm tall and the north wall follows the raised compartment;
the joint stops 2.5 mm below either rim.  The plug bottom is 1.5 mm above the
tab shelf, so the dovetail clears the bottom tray.  The slot is hidden inside
the wall and the top rim is solid except for the straight butt seam.  Glue the
joints: the cap prevents the rail lifting, while the open bottom means the rail
must be held down by glue, the plate or the tray.  The dovetails' undersides
overhang the lower recess / open slot by about **3.6 mm**; print both rails with
support under the dovetail, or orient them so that face is not a downward
overhang.

The plate, bottom tray, cover and tilt bar are not split by this feature.  Each
piece is validated strictly and its X extent is checked against the 250 mm
target; if a 1:2:1 split would still leave a piece too wide, generation raises
instead of exporting it.

### Plate support and wire routing

The plate-pocket floor is recessed by `wire_gap` (1.5 mm) and gets one short
**3 x 10 mm rib in each gap between neighbouring keycaps**, at the original
floor level.  The ribs sit under the cap boundaries, which are solid plate even
for wide keys; they are clear of the switch pockets and socket tabs.  The plate
rests on them; wires run in the channels between.  The recess is open to the
bay at the plate's north edge.  It is inset **0.5 mm** from the west, east and
south pocket walls so the recess keeps a solid rim under the plate edge.  There
is no perimeter rib.

### Cover magnets

The cover lid stays **2.5 mm** thick.  Only the magnet locations are thickened:
each magnet gets a local **1.5 mm boss** on the cover underside.  The bosses
land on small 45-degree shelves inside the bay; the rest of the lid rests on the
bay shelf at 27.5 mm.  The exposed front-top edge is chamfered by
**COVER_TOP_BEVEL = 0.8 mm at 45 degrees** so the long right-angle edge above
the top row of keys is not sharp.  The west/east lid ledges are notched back
4.7 mm at the bay's front so the cover's front wall clears them; the side magnet
shelves keep their 2.7 mm set-back and their front ends stay hidden under the
cover.

Standard magnets are **6 x 3 x 3 mm**.  The longest side follows the edge:
6 mm along Y on the side edges, 6 mm along X on the top edge, and 3 mm
vertical.  **Each magnet gets its own small shelf.**  There is no continuous
side or top rail:

- each side edge carries **two magnets**, long side along Y, placed end-to-end
  with a narrow gap; if the side span is too short for two, it gets **one
  centered magnet** instead.  Generation fails only if even one cannot fit;
- the top (north) edge starts east of the USB-C recess and carries an adaptive
  evenly spaced row of magnets with their long sides along X:
  `count = ceil(available_width / magnet_spacing)`, capped by what fits.  The
  default spacing is 40 mm, giving 2 top magnets on the numpad, 7 on the
  60%/75%, and 11 on the 100% (6/11/11/15 total with the side magnets).

If `magnet_width` is too large for even one side magnet, generation fails; when
it only fits one, that side gets one centered magnet.  The shelves are separate
pads, unioned one by one; adjacent side pads have a narrow gap between them.

Each small shelf has a flat top at the magnet height and a **45-degree
underside** running back to the cavity wall.  It does not extend down to the
floor.  The shelf top is 1.5 mm below the bay shelf (26.0 mm default), so the
boss bottoms and the magnets meet there.  Both the shelf and the boss/lid get a
pocket `magnet + 0.4 mm` across and `magnet thickness + 0.1 mm` deep; the cover
keeps a 0.9 mm top skin.  The front lip still hangs to the plate top and locates
the lid in Y.

When the compartment is raised, the outer shell gets its normal chamfer at the
raised height, then the ramp cutter lowers the key-side section to the 15 mm rim.
That newly exposed lower rim is chamfered **after** all cuts in
`_bevel_lower_rim`, so the key-side rim keeps the same 2.0 mm top bevel as the
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

- case edges `#66D9EF` cyan,
- case bottom `#4FB8CE` darker cyan (shows the split),
- cover `#F92672` pink,
- top plate `#E6DB74` yellow,
- background `#272822`.

The previewer is not a model generator and does not write STLs. It uses the
default plate/split-case parameters, so it matches the default generated parts.

For inspecting one part from an arbitrary angle, use `render_part.py`:

```sh
blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
  --keyboard kbNumpad --part cover --azimuth 180 --elevation -55
```

The cover's front lip hides its underside from a low south view; use azimuth
180 (north) or a side angle for the magnet bosses.

Options:

- `--keyboard ...`, `--part plate|case|case_bottom|case_edges|cover|tilt|assembly|slider`
- `--stl PATH` — render an existing STL instead of building a part
- `--azimuth DEG` — 0 south, 90 east, 180 north, 270 west
- `--elevation DEG` — negative values look from below
- `--orthographic`, `--margin F`
- `--samples N`, `--width PX`, `--height PX`, `--colour #RRGGBB`
- `--output PATH`

The default output is `build/<keyboard>_<part>_az<angle>_el<angle>.png`.

### Exploded view

`render_exploded.py` separates the parts into layers: the bottom tray at the
bottom, the edge ring in the middle (the two U side pieces of a wide case spread
outward; the two rails stay at their wall positions), the top plate above them
and the cover at the very top:

```sh
blender --background --python-exit-code 1 --python keyboards/render_exploded.py -- \
  --keyboard kb75
```

Options:

- `--keyboard kbNumpad|kb60|kb75|kb100`
- `--split-plate` — show the plate as its two horizontal halves
- `--spread MM` — west/east side-piece spread along X (default 50 mm); the two
  middle rails stay at their wall positions
- `--gap MM` — bottom tray to edge ring (default 50 mm)
- `--plate-gap MM` — edge-ring top to plate (default 45 mm)
- `--plate-half-gap MM` — gap between split plate halves (default 14 mm)
- `--cover-gap MM` — plate top to cover (default 30 mm)
- `--azimuth DEG`, `--elevation DEG` — camera orbit (default 35/30)
- `--orthographic`, `--margin F`
- `--samples N`, `--width PX`, `--height PX`
- `--output PATH` — default `build/<keyboard>_exploded.png`

The wide boards' edge pieces keep their dovetails; the numpad's one-piece ring
stays together. The script builds the same default parts as the generator and
writes only a PNG.

### Dovetail close-ups

`render_dovetail.py` renders the male plug and the female slot as two close-ups:
the male rail from the east and the side piece from the west:

```sh
blender --background --python-exit-code 1 --python keyboards/render_dovetail.py -- \
  --keyboard kb60 --side north
```

Options:

- `--keyboard kbNumpad|kb60|kb75|kb100`
- `--side north|south` — which wall's dovetail (default north)
- `--output BASE` — optional base path; `_male` and `_female` are appended
- `--samples N`, `--width PX`, `--height PX`

The default outputs are `build/<keyboard>_dovetail_<side>_male.png` and
`build/<keyboard>_dovetail_<side>_female.png`.  The plug's lower end and the
slot's open bottom / top cap are visible.

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
7. Do not duplicate plate/case/cover/tilt code in the layout file.

Example skeleton:

```python
NAME = "kbXX"
KEYS = layout.keys_from_rows(ROWS)

args = cli.parse_args(argv, description=f"{NAME} ({len(KEYS)} keys)")
params = cli.params_from_args(args)
plate, case_bottom, case_edges, cover, tilt_path = cli.output_paths(
    args.output, *cli.default_paths(PROJECT_DIR, NAME)
)
spec = generate.KeyboardSpec(
    name=NAME,
    keys=KEYS,
    plate_path=plate,
    case_bottom_path=case_bottom,
    case_edges_path=case_edges,
    cover_path=cover,
    tilt_path=tilt_path,
    params=params,
    compartment_height=args.compartment_height,
    compartment_ramp=args.compartment_ramp,
    wall=args.wall,
    compartment_depth=args.compartment_depth,
    split_plate=args.split_plate,
    options=cli.mounting_from_args(args),
)
generate.generate_keyboard(spec)
```

### Definition of done for a new keyboard

- `blender --background --python-exit-code 1 --python keyboards/<name>/main.py`
  exits 0.
- Default mode creates the `build/<name>/` tree: one `top_plate/` file (or two
  with `--split-plate`), `case/` with the bottom tray plus one edge ring or
  four pieces, one `cover/` file and one `tilt/` file. Above 250 mm case width
  the edge ring becomes four pieces instead of one.
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

## Tests and validation

Pure layout and mounting plans:

```sh
python3 -m unittest discover -s tests -p "test_*.py" -v
```

Geometry metrics for all four boards, a real numpad plate/case-bottom/
case-edges/cover/split build with the tilt-bar sockets and bar, a split kb60
edge ring and the south-facing 100% plate:

```sh
blender --background --python-exit-code 1 --python tests/run_blender_tests.py
```

Generation remains the integration test, and the mesh validator is strict:

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

## Development loop

1. Make the change.
2. Regenerate all four keyboards (the loop above) and/or run the tests.
3. If a part fails validation or looks wrong, render the affected part from the
   relevant angles and inspect the PNG before changing code again:

```sh
blender --background --python-exit-code 1 --python keyboards/render_part.py -- \
  --keyboard kb75 --part case_edges --azimuth 35 --elevation 35
```

`render_part.py` builds one part (or the assembly, the tilt bar, or a slider),
frames it automatically and can also load an arbitrary STL with `--stl`.
Negative elevation looks from below; `--orthographic` gives a
dimension-friendly view.

## Known constraints and pitfalls

- **Hotswap pocket size:** the current validated pocket is 14 x 9 mm (the
  12 x 9 mm body plus a 2 mm extension on the +X side) plus two 3 x 3 mm contact
  tabs. A literal 11 x 7 mm pocket produced non-manifold slivers at the
  switch-leg openings.
- **Hotswap tab order:** both tabs must be cut after the through-holes. The
  socket body is cut before them.
- **South facing:** all switch/hotswap/stabilizer cutouts rotate 180 degrees.
  Use the matching `--led-facing south` plates; do not mix north and south.
- **Vertical stabilizers:** default wire direction is east; the slider rotation
  differs for south-facing vertical keys.
- **Case shelf:** the USB glue shelf is an internal cantilever and may need a
  support on some printers.
- **100% size:** the split 100% case is about 447 mm wide; confirm the printer
  bed size before printing.
- **Split-case location:** the plate sits between the bottom tray and the
  edge-ring shelf and is located by the tray lip, edge-ring recess and the
  south triangular ledge. Six 3 mm wood screws clamp the stack: two through the
  tray into blind plate pilots and four into the north-wall boxes. Do not
  reintroduce wall notches or plate ears, and keep the plate pilots clear of
  stabilizer channels and post holes.
- **Edge-ring lower skin:** the straight recess leaves about 0.55 mm of outer
  wall at the very bottom above the 2.0 mm chamfer; the wall option is limited
  to 7 mm minimum. Revalidate all four boards if the tab sizes change.
- **Tab split:** tabs are exactly 4 mm high and the split is at z = 3.75 mm.
  The bottom half carries the lower 3.75 mm of the tabs; the top half carries
  only the top 0.25 mm edge.
- **Assembly order:** put the plate on the bottom tray, then lower the edge ring
  over it: the south triangular ledge enters the tray's side dent and the north
  screw boxes enter the lip cutouts. When the edge ring is split, place the two
  middle rails first and lower the side pieces onto them from above; their
  slots are open at the bottom so the plugs enter from below. Glue the joints.
  Drive the six screws from below. The plate cannot be dropped in from above
  after the edge ring is seated because the edge shelf overhangs the tabs.
- **Wide case split:** the edge ring splits automatically above 250 mm case
  width, at ±`width / 4`. The slot is open at the bottom and capped 2.5 mm below
  the top; the male dovetail starts 1.5 mm above the tab shelf, so it clears the
  bottom tray. The dovetails lock only along X; glue the joints. The cap stops
  the rails lifting, but the open bottom means the rails must be retained by
  glue, the plate or the tray. Both dovetails' undersides are a ~3.6 mm
  overhang over the lower recess / open slot, so support both rails when
  printing them. Re-run all four boards after changing the `case_split`
  constants or the wall/top-chamfer geometry.
- **Tilt bar:** it is always one printed part at **80% of the case width**, by
  design; do not split it. The numpad and 60% bars fit a 250 mm bed; the 75%
  (259 mm) and 100% (358 mm) bars need a bed at least that wide. The tilt
  magnets are fixed at 6 x 3 x 3 mm, independent of `--magnet-*` (those options
  only change the cover magnets). The bar sits at the bay start, with pins in
  front and magnets behind; the tray has matching pin sockets and internal
  magnet cavities. Both magnet sets are enclosed by a 0.6 mm printed cover:
  pause at the top of the cavity, insert, resume. The bar pocket is 3.1 mm
  high; the tray pocket is 3.0 mm high with a 0.4 mm bottom skin. Test magnet
  polarity before closing the covers. Pin sockets are 2.6 mm deep and still
  open on the print-bed face. The sockets stay clear of the bottom-tray x = 0
  split and of the wire recess.
- **Tiny boolean holes:** the 100% south-facing plate can leave a sub-millimetre
  boundary loop where a hotswap pocket corner meets a switch leg hole.
  `mesh.fill_small_holes()` closes only loops under 2 mm perimeter; larger
  boundary loops are still rejected by the strict mesh check.
- **Inter-key ribs:** the case support ribs are deliberately short and placed
  only in keycap gaps; there is no continuous rib or perimeter rib.
- **Compatibility shims:** do not add features to `keyboard_parts.py` or
  `keyboard_case.py`.
- **Build PNGs:** STLs and working previews under `build/` are generated
  artifacts. Documentation previews in `screenshots/` are intentionally kept
  for README/SCREENSHOTS.

## Future Corne and Dactyl

- **Corne:** standard 3x6 + 3 thumb per half, split. The flat plate builder and
  layout model can be reused per half. The split case here is a two-part case
  for one board, not a left/right split; Corne still needs a dedicated
  case/cover builder.
- **Dactyl:** curved key wells. A new curved builder is required; the shared
  switch/LED/hotswap dimensions and mesh validation from `hardware.py`/`mesh.py`
  should be reused with 3D transforms. The current flat cup/plate builder is not
  sufficient.
- Keep placeholders until a real generator exists. A placeholder must not write
  STLs and should exit 1.

## Git hygiene

- `.gitignore` ignores `*.stl`, `*.STL`, `.dsh/` and `__pycache__/`;
  `keyboards/.gitignore` also ignores its own `__pycache__/`.
- Commit sources: `AGENTS.md`, `README.md`, `SCREENSHOTS.md`, `.gitignore`,
  shared modules, layout `main.py` files, preview and stabilizer scripts, and the
  documentation previews in `screenshots/`.
- Never commit generated STLs, working previews under `build/`, Blender backups
  (`*.blend1`) or `__pycache__`.
- Keep generated files under `build/` so source directories stay clean.
