# Screenshots and generation commands

These renders use the current defaults:

- 2 mm plate padding on all sides
- 7 mm case walls
- 17.5 mm-deep electronics bay
- 27.5 mm-tall compartment
- north-facing switches/LEDs
- Monokai colors: cyan case, pink cover, yellow top plate

Each section lists the command that generates the STLs and the command that
renders the preview image. Run all commands from the repository root.

## Generate all four keyboards

```sh
blender --background --python-exit-code 1 --python keyboards/kbNumpad/main.py
blender --background --python-exit-code 1 --python keyboards/kb60/main.py
blender --background --python-exit-code 1 --python keyboards/kb75/main.py
blender --background --python-exit-code 1 --python keyboards/kb100/main.py
```

The preview renderer is separate from STL generation:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kbNumpad
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb60
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb75
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb100
```

## Numpad — 17 keys

![Numpad preview](build/kbNumpad_preview.png)

Generate:

```sh
blender --background --python-exit-code 1 --python keyboards/kbNumpad/main.py
```

Render:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kbNumpad
```

Outputs:

```text
build/kbNumpad_top_plate.stl
build/kbNumpad_case.stl
build/kbNumpad_cover.stl
build/kbNumpad_preview.png
```

Plate 80.2 × 99.25 × 9.5 mm · case 94.7 × 131.25 × 27.5 mm ·
cover 80.3 × 17.3 × 15.0 mm.

## 60% — ANSI 61 keys

![60% preview](build/kb60_preview.png)

Generate:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py
```

Render:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb60
```

Outputs:

```text
build/kb60_top_plate.stl
build/kb60_case.stl
build/kb60_cover.stl
build/kb60_preview.png
```

Plate 289.75 × 99.25 × 9.5 mm · case 304.25 × 131.25 × 27.5 mm ·
cover 289.85 × 17.3 × 15.0 mm.

## 75% — 83 keys

![75% preview](build/kb75_preview.png)

Generate:

```sh
blender --background --python-exit-code 1 --python keyboards/kb75/main.py
```

Render:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb75
```

Outputs:

```text
build/kb75_top_plate.stl
build/kb75_case.stl
build/kb75_cover.stl
build/kb75_preview.png
```

Plate 308.8 × 118.3 × 9.5 mm · case 323.3 × 150.3 × 27.5 mm ·
cover 308.9 × 17.3 × 15.0 mm.

## 100% — ANSI 104 keys

![100% preview](build/kb100_preview.png)

Generate:

```sh
blender --background --python-exit-code 1 --python keyboards/kb100/main.py
```

Render:

```sh
blender --background --python-exit-code 1 --python keyboards/render_preview.py -- --keyboard kb100
```

Outputs:

```text
build/kb100_top_plate.stl
build/kb100_case.stl
build/kb100_cover.stl
build/kb100_preview.png
```

Plate 432.62 × 118.3 × 9.5 mm · case 447.12 × 150.3 × 27.5 mm ·
cover 432.73 × 17.3 × 15.0 mm.

## Stabilizer slider

The keyboard previews do not show the stabilizer sliders. Generate the slider
separately:

```sh
blender --background --python-exit-code 1 --python keyboards/stabilizers/main.py
```

Output:

```text
build/stabilizer_slider.stl
```

Print two per stabilized key. The script also prints the wire cut lengths for
2u, 2.25u, 2.75u, 3u, 6u, 6.25u and 7u.

## Custom variants

Use `--output` to avoid overwriting the defaults. For example, a south-facing
60%:

```sh
blender --background --python-exit-code 1 --python keyboards/kb60/main.py -- \
  --led-facing south --output build/kb60_south.stl
```

This writes:

```text
build/kb60_south.stl
build/kb60_south_case.stl
build/kb60_south_cover.stl
```

Screenshots and STLs in `build/` are generated artifacts; they are not source
files.
