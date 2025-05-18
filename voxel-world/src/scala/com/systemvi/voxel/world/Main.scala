package com.systemvi.voxel.world

import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import org.joml.{Vector3f, Vector3i}

object Main {
  def main(args: Array[String]): Unit = {
    val config=DemoAppConfig(
      generator = PerlinWorldGenerator(0f,200f,0.3f),
      numberOfChunks = Vector3i(20,10,20),
      initialLightParams = InitialLightParams(
        position = Vector3f(-50, 200, -50),
        rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0),
      )
    )

    DemoApp(config).run()
  }
}
