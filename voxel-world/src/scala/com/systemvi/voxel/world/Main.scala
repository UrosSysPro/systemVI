package com.systemvi.voxel.world

import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import org.joml.{Vector3f, Vector3i}

object Main {
  def largeWorldConfig=DemoAppConfig(
    generator = PerlinWorldGenerator(0f,200f,0.3f),
    numberOfChunks = Vector3i(20,10,20),
    initialLightParams = InitialLightParams(
      position = Vector3f(-50, 200, -50),
      rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0),
      far=1000f,
      near=0.1f,
    ),
    initialPlayerPosition = Vector3f(-1,150,-1)
  )

  def smallWorldConfig=DemoAppConfig(
    generator = PerlinWorldGenerator(0f,32f,0.1f),
    numberOfChunks = Vector3i(2,2,2),
    initialLightParams = InitialLightParams(
      position = Vector3f(-50, 100, -50),
      rotation = Vector3f(-Math.PI.toFloat / 4f, -Math.PI.toFloat * 3f / 4f, 0),
      far=100f,
      near=0.1f,
    ),
    initialPlayerPosition = Vector3f(-1,32,-1)
  )
  def main(args: Array[String]): Unit = {
    val config = largeWorldConfig

    DemoApp(config).run()
  }
}
