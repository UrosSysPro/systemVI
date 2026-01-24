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

    for {
      _ <- apps.traverse{a=>applications.add(a)}
    } yield ()
  }
}
