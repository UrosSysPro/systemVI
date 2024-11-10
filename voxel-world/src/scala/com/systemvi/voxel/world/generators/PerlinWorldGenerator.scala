package com.systemvi.voxel.world.generators

import com.systemvi.engine.noise.{FractalNoise2d, Perlin2d}
import com.systemvi.voxel.world.world.Block
import org.joml.{Vector2f, Vector3i}

class PerlinWorldGenerator extends WorldGenerator {
  
  val noise=FractalNoise2d.harmonicsPerlin(4)

  override def get(worldPosition: Vector3i): Block = {
    val base=50f
    val variation=100f
    val scale=0.5f
    val floorLevel:Int=(base+noise.get(Vector2f(worldPosition.x.toFloat/16*scale,worldPosition.z.toFloat/16*scale))*variation).toInt
    floorLevel match
      case y:Int if y==worldPosition.y=>Block.DIRT
      case y:Int if y>worldPosition.y=>Block.STONE
      case y:Int if y<worldPosition.y=>Block.AIR
  }
}
