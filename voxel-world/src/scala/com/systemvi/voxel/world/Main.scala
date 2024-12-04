package com.systemvi.voxel.world

import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import org.joml.{Vector3f, Vector3i}

object Main {
  def main(args: Array[String]): Unit = {
    val config=DemoAppConfig(
      generator = PerlinWorldGenerator(5f,20f,0.5f),
      numberOfChunks = Vector3i(2,2,2),
      initialLightParams = InitialLightParams(
        position = Vector3f(-10, 60, -10),
        rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0)
      )
    )

    DemoApp(config).run()
  }
}
