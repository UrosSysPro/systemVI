package net.systemvi.server.persistance.seeders

import cats.*
import cats.implicits.*
import cats.effect.*
import doobie.util.transactor.Transactor
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.contexts.*
import java.util.UUID

object ApplicationSeeders {
  def seed(xa:Transactor[IO]): IO[Unit] = {
    val images = EntityImageContext.create(xa)
    val applications = ApplicationContext.create(xa)

    var apps = List.empty[Application]
    def addApp(a: Application):Application = {
      apps = apps :+ a
      a
    }

    var imgs = List.empty[EntityImage]
    def addImg(l: List[EntityImage]): List[EntityImage] = {
      imgs = imgs ++ l
      l
    }

    val normalMapping                   = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Normal Mapping",           "normal_mapping","Demo of blin-phong shading with normal mapping."))
    val box2dDemo                       = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Box2d Demo",               "box2d_demo",  "Simple demo of box2d rigid body physics"))
    val rayMarchingGpu                  = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Ray Marching (GPU)",       "ray_marching_gpu","Simple demo of box2d rigid body physics"))
    val assetImport                     = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Asset Importer (ASSIMP)",  "assimp","Simple demo of box2d rigid body physics"))
    val mazeGeneration                  = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Maze Generation",          "maze_generation","Simple demo of box2d rigid body physics"))
    val fragmentShaderFractals          = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Fragment Shader Fractals", "fragment_shader_fractals","Simple demo of box2d rigid body physics"))
    val fluidSimulationGpu              = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Fluid Simulation (GPU)",   "fluid_sim_gpu","Simple demo of box2d rigid body physics"))
    val fabrik                          = addApp(Application(UUID.randomUUID(),TechDemo.id,   "F.A.B.R.I.K.",             "fabrik","Simple demo of box2d rigid body physics"))
    val reactionDiffusionSimulationCpu  = addApp(Application(UUID.randomUUID(),TechDemo.id,   "Reaction Diffusion (CPU)", "reaction_diffusion_sim_cpu","Simple demo of box2d rigid body physics"))

    val breakOut                        = addApp(Application(UUID.randomUUID(),Game.id,       "Atari Break Out",          "atari_break_out","Simple demo of box2d rigid body physics"))
    val snake                           = addApp(Application(UUID.randomUUID(),Game.id,       "Snake",                    "snake","Simple demo of box2d rigid body physics"))
    val voxels                          = addApp(Application(UUID.randomUUID(),Game.id,       "Voxels",                   "voxels","Simple demo of box2d rigid body physics"))
    val flappyBird                      = addApp(Application(UUID.randomUUID(),Game.id,       "Flappy Bird",              "flappy_bird","Simple demo of box2d rigid body physics"))

    val keyboardConfigurator            = addApp(Application(UUID.randomUUID(),Tool.id,       "Keyborad Configurator",    "keyboard_configurator","Simple demo of box2d rigid body physics"))

    val normalMappingImgs = addImg(List(
      EntityImage(normalMapping.uuid,"/images/engine/normal-mapping.png",1)
    ))
    val box2dImgs = addImg(List(
      EntityImage(box2dDemo.uuid,"/images/engine/box2d.png",1)
    ))
    val rayMarchingImgs = addImg(List(
      EntityImage(rayMarchingGpu.uuid,"/images/engine/ray-marching-gpu.png",1),
    ))
    val assimpImgs = addImg(List(
      EntityImage(assetImport.uuid,"/images/engine/model-importer.png",1),
    ))
    val mazeGenerationImgs = addImg(List(
      EntityImage(mazeGeneration.uuid,"/images/engine/maze.png",1),
    ))
    val fragmentShaderFractalImgs = addImg(List(
      EntityImage(fragmentShaderFractals.uuid, "/images/engine/mandelbrot-set.png",1),
      EntityImage(fragmentShaderFractals.uuid,"/images/engine/jullia-set.png",2),
    ))
    val fluidSimulationGpuImgs = addImg(List(
      EntityImage(fluidSimulationGpu.uuid, "/images/engine/fluid-gpu.png",1),
    ))
    val fabrikImgs = addImg(List(
      EntityImage(fabrik.uuid, "/images/engine/fabrik.png",1),
    ))
    val reactionDiffusionImgs = addImg(List(
      EntityImage(reactionDiffusionSimulationCpu.uuid, "/images/engine/reaction-diffusion.png",1),
    ))
    val atariBreakoutImgs = addImg(List(
      EntityImage(breakOut.uuid, "/images/games/breakout.png",1),
    ))
    val voxelImgs = addImg(List(
      EntityImage(voxels.uuid, "/images/games/voxels1.png",1),
      EntityImage(voxels.uuid, "/images/games/voxels2.png",2),
      EntityImage(voxels.uuid, "/images/games/voxels3.png",3),
      EntityImage(voxels.uuid, "/images/games/voxels4.png",4),
    ))
    val snakeImgs = addImg(List(
      EntityImage(snake.uuid, "/images/games/snake.png",1),
    ))
    val flappyBirdImgs = addImg(List(
      EntityImage(flappyBird.uuid, "/images/games/flappy-bird.png",1),
    ))
    val keyboardConfiguratorImgs = addImg(List(
      EntityImage(keyboardConfigurator.uuid,"images/application/configurator/configurator.png",1),
      EntityImage(keyboardConfigurator.uuid,"images/application/configurator/tester.png",2),
      EntityImage(keyboardConfigurator.uuid,"images/application/configurator/save-and-load.png",3),
      EntityImage(keyboardConfigurator.uuid,"images/application/configurator/settings.png",4),
    ))

    for {
      _ <- apps.traverse{a => applications.add(a)}
      _ <- imgs.traverse{i => images.add(i)}
    } yield ()
  }
}
