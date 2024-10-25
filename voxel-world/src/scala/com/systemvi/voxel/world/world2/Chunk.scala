package com.systemvi.voxel.world.world2

import com.systemvi.voxel.world.generators.WorldGenerator
import com.systemvi.voxel.world.world.BlockState
import org.joml.Vector3i

class Chunk {

  import Chunk.size

  val blockStates: Array[Array[Array[BlockState]]] = Array.ofDim(size.x, size.y, size.z)


  def generate(generator: WorldGenerator, chunkPosition: Vector3i): Unit = {
    val worldPosition = Vector3i()
    for (i <- 0 until size.x; j <- 0 until size.y; k <- 0 until size.z) {
      worldPosition.set(
        chunkPosition.x * size.x + i,
        chunkPosition.y * size.y + j,
        chunkPosition.z * size.z + k,
      )
      blockStates(i)(j)(k)=BlockState(generator.get(worldPosition))
    }
  }
}

object Chunk {
  val size = Vector3i(16, 16, 16)
}
