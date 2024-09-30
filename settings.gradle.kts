rootProject.name = "systemVI"
include ("voxel-world")
include ("examples")
include ("engine")

val examples=listOf(
    "generative-shaders",
    "normal-mapping",
    "lanterna-powerline",
    "break-out",
    "fluid",
    "multi-window",
    "collections",
    "flappybird",
    "diffusion",
    "fractals",
    "paint",
    "cats",
    "triangle",
    "fabrik",
    "test",
    "instancing",
    "game-of-life",
    "phong",
    "sdf"
)
examples.forEach{
    include("examples:$it")
}


