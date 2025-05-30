package net.systemvi.website.api

import net.systemvi.website.model.*

private val keyboards=List[Keyboard](
  Keyboard(
    id = 1,
    name = "Corne Wireless",
    codeName = "corne_wireless",
    specs = List(
      KeyboardSpec(name = "spec 1", value = "value 1"),
      KeyboardSpec(name = "spec 2", value = "value 2"),
      KeyboardSpec(name = "spec 3", value = "value 3"),
      KeyboardSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
    )
  ),
  Keyboard(
    id = 2,
    name = "Corne Prototype",
    codeName = "corne_prototype",
    specs = List(
      KeyboardSpec(name = "spec 1", value = "value 1"),
      KeyboardSpec(name = "spec 2", value = "value 2"),
      KeyboardSpec(name = "spec 3", value = "value 3"),
      KeyboardSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
      "/images/corne-prototype.jpg",
    )
  ),
  Keyboard(
    id = 3,
    name = "PH Design 60%",
    codeName = "ph_design_60",
    specs = List(
      KeyboardSpec(name = "spec 1", value = "value 1"),
      KeyboardSpec(name = "spec 2", value = "value 2"),
      KeyboardSpec(name = "spec 3", value = "value 3"),
      KeyboardSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
      "/images/keyboard-60.jpg",
    )
  ),
  Keyboard(
    id = 4,
    name = "Bana 40%",
    codeName = "bana_40",
    specs = List(
      KeyboardSpec(name = "spec 1", value = "value 1"),
      KeyboardSpec(name = "spec 2", value = "value 2"),
      KeyboardSpec(name = "spec 3", value = "value 3"),
      KeyboardSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
      "/images/red-keyboard.jpg",
    )
  ),
  Keyboard(
    id = 5,
    name = "TKL Rabbit",
    codeName = "tkl_rabbit",
    specs = List(
      KeyboardSpec(name = "spec 1", value = "value 1"),
      KeyboardSpec(name = "spec 2", value = "value 2"),
      KeyboardSpec(name = "spec 3", value = "value 3"),
      KeyboardSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
      "/images/tkl-rabbit.jpg",
    )
  ),
  Keyboard(
    id = 6,
    name = "7x5 Dactyl",
    codeName = "dactyl",
    specs = List(
      KeyboardSpec(name = "spec 1", value = "value 1"),
      KeyboardSpec(name = "spec 2", value = "value 2"),
      KeyboardSpec(name = "spec 3", value = "value 3"),
      KeyboardSpec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
      "/images/dactyl2.jpg",
    )
  ),
)

private val defaultKeyboard=Keyboard(
  id = -1,
  name = "Default Keyboard",
  codeName = "default_code_name",
  specs = List(
    KeyboardSpec(name = "spec 1", value = "value 1"),
  ),
  images = List(
    "/images/corne-wireless.jpg",
  )
)

object KeyboardApi {
  def all():List[Keyboard]=keyboards
  def get(id:Int):Keyboard=keyboards.find(_.id==id).getOrElse(defaultKeyboard)
}

private val games=List[Game](
  Game(1,"Atari Break Out","break_out",List("/images/games/breakout.png")),
  Game(2,"Snake","snake",List("/images/games/snake.png")),
  Game(3,"Voxels","voxels",List(
    "/images/games/voxels1.png",
    "/images/games/voxels2.png",
    "/images/games/voxels3.png",
    "/images/games/voxels4.png",
  )),
  Game(4,"Flappy Bird","flappy_bird",List("/images/games/flappy-bird.png")),
)

private val defaultGame=Game(-1,"Default Game","default_code_name",List("aaa"))

object GameApi {
  def all():List[Game]=games
  def get(id:Int):Game=games.find(_.id==id).getOrElse(defaultGame)
}

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