package com.systemvi.voxel.world.generators
import com.systemvi.engine.noise.Perlin2
import com.systemvi.voxel.world.world.Block
import org.joml.Vector3i

class PerlinWorldGenerator extends WorldGenerator {
  val noise=Perlin2()
  
  override def get(block: Vector3i): Block = {
    Block.STONE
  }
}
