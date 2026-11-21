# Tests

The layout and mounting plans are pure Python and can be tested without
Blender:

```sh
python3 -m unittest discover -s tests -p "test_*.py" -v
```

The geometry tests check the derived metrics for all four boards, build the
numpad plate, case bottom, case edges, cover and split plate through the real
builders, and split the 60% edge ring into its four dovetailed pieces:

```sh
blender --background --python-exit-code 1 --python tests/run_blender_tests.py
```

No third-party test framework is required.
