package com.systemvi.voxel.world.generators

import com.systemvi.engine.noise.Perlin2
import com.systemvi.voxel.world.world.Block
import org.joml.{Vector2f, Vector3i}

class PerlinWorldGenerator extends WorldGenerator {
  val noise=Perlin2()

  override def get(worldPosition: Vector3i): Block = {
    val base=10
    val variation=10
    val floorLevel:Int=(base+noise.get(Vector2f(worldPosition.x.toFloat/16,worldPosition.z.toFloat/16))*variation).toInt
    print(s"$floorLevel ")
    if worldPosition.y<floorLevel then Block.STONE else Block.AIR
  }
}
