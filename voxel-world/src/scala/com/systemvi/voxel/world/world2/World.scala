package com.systemvi.voxel.world.world2

import com.systemvi.voxel.world.generators.WorldGenerator
import org.joml.Vector3i


class World {
  val chunks:Array[Array[Array[Chunk]]]=Array.ofDim(2,2,2)
  
  
  def generate(generator: WorldGenerator): Unit = {
    for(i <- chunks.indices;j<-chunks(0).indices;k<-chunks(0)(0).indices){
      chunks(i)(j)(k)=Chunk()
      val chunk=chunks(i)(j)(k)
      chunk.generate(generator = generator, chunkPosition = Vector3i(i,j,k))
    }
  }
}
