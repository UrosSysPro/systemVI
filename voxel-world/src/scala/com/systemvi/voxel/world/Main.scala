package com.systemvi.voxel.world

import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import org.joml.Vector3i

object Main {
  def main(args: Array[String]): Unit = {
    val config=DemoAppConfig(
      generator = PerlinWorldGenerator(50f,100f,0.5f),
      numberOfChunks = Vector3i(20,10,20),
    )

    DemoApp(config).run()
  }
}
