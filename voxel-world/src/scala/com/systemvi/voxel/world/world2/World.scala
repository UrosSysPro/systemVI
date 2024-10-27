package com.systemvi.voxel.world.world2

import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.voxel.world.generators.WorldGenerator
import com.systemvi.voxel.world.world.Block
import org.joml.Vector3i


class World {

  val texture=Texture("assets/examples/minecraft/textures/diffuse.png")
  val regions: Array[Array[TextureRegion]] =TextureRegion.split(texture,16,16)
  Block.AIR = new Block(null, null, null, false)
  Block.STONE = new Block(regions(7)(0), regions(7)(0), regions(7)(0), true)
  Block.DIRT = new Block(regions(2)(3), regions(1)(2), regions(1)(3), true)

  val chunks:Array[Array[Array[Chunk]]]=Array.ofDim(1,1,1)
  
  
  def generate(generator: WorldGenerator): Unit = {
    for(i <- chunks.indices;j<-chunks(0).indices;k<-chunks(0)(0).indices){
      chunks(i)(j)(k)=Chunk()
      val chunk=chunks(i)(j)(k)
      chunk.generate(generator = generator, chunkPosition = Vector3i(i,j,k))
    }
  }
}
