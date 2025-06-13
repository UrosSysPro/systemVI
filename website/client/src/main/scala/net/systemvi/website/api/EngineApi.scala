package net.systemvi.website.api

import net.systemvi.common.model.*

private val defaultEngine=Engine(
  id=1,
  name = "systemVI",
  codeName = "systemvi",
  version = "0.8.3",
  description = "Library for making apps and games in scala",
  demos = List(
    EngineDemo(
      id=1,
      name = "Normal Mapping",
      description = "Phong renderer using normal mapping",
      images = List("/images/engine/normal-mapping.png")
    ),
    EngineDemo(
      id=2,
      name = "Box2d Bindings",
      description = "Box2d bindings for systemVI game engine",
      images = List("/images/engine/box2d.png")
    ),
    EngineDemo(
      id=3,
      name = "Ray Marching",
      description = "Ray Marching demo using signed distance fields implemented in systemVI",
      images = List(
        "/images/engine/ray-marching-gpu.png",
        "/images/engine/ray-marching-cpu.png",
      )
    ),
    EngineDemo(
      id=4,
      name = "Assets Importer (Assimp) Bindings",
      description = "Assimp bindings for systemVI game engine",
      images = List("/images/engine/model-importer.png")
    ),
    EngineDemo(
      id=5,
      name = "Maze generation",
      description = "Maze generation algorithm implemented using randomized depth first search",
      images = List("/images/engine/maze.png")
    ),
    EngineDemo(
      id=6,
      name = "Fragment Shader Fractals",
      description = "Gpu accelerated rendering of fractals using fragment shaders",
      images = List(
        "/images/engine/mandelbrot-set.png",
        "/images/engine/jullia-set.png",
      )
    ),
    EngineDemo(
      id=7,
      name = "Fluid Simulation",
      description = "Fluid simulation implementation on cpu and gpu using compute shaders",
      images = List(
        "/images/engine/fluid-gpu.png",
        "/images/engine/fluid-cpu.png",
      )
    ),
    EngineDemo(
      id=8,
      name = "Fabrik",
      description = "forwards and backwards reaching inverse kinematics",
      images = List(
        "/images/engine/fabrik.png",
      )
    ),
    EngineDemo(
      id=9,
      name = "Reaction Diffusion",
      description = "Reaction diffusion simulation implemented on cpu",
      images = List(
        "/images/engine/reaction-diffusion.png",
      )
    ),
  )
)

object EngineApi {
  def get():Engine=defaultEngine
}
