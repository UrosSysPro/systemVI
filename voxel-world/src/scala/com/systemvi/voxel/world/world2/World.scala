package com.systemvi.voxel.world.world2

import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.voxel.world.generators.WorldGenerator
import com.systemvi.voxel.world.world.{Block, BlockState}
import org.joml.Vector3i


class World(val numberOfChunks: Vector3i) {

  val texture = Texture("assets/examples/minecraft/textures/diffuse.png")
  val regions: Array[Array[TextureRegion]] = TextureRegion.split(texture, 16, 16)
  Block.AIR = new Block(null, null, null, false)
  Block.STONE = new Block(regions(7)(0), regions(7)(0), regions(7)(0), true)
  Block.DIRT = new Block(regions(2)(3), regions(1)(2), regions(1)(3), true)

  val chunks: Array[Array[Array[Chunk]]] = Array.ofDim(numberOfChunks.x, numberOfChunks.y, numberOfChunks.z)

  def getBlockState(worldPosition: Vector3i): BlockState = getBlockState(
    worldPosition.x / Chunk.size.x,
    worldPosition.y / Chunk.size.y,
    worldPosition.z / Chunk.size.z,
    worldPosition.x % Chunk.size.x,
    worldPosition.y % Chunk.size.y,
    worldPosition.z % Chunk.size.z,
  )

  def getBlockState(chunkPosition: Vector3i, blockPosition: Vector3i): BlockState = {
    if (
      chunkPosition.x < 0 || chunkPosition.x >= numberOfChunks.x ||
        chunkPosition.y < 0 || chunkPosition.y >= numberOfChunks.y ||
        chunkPosition.z < 0 || chunkPosition.z >= numberOfChunks.z ||
        blockPosition.x < 0 || blockPosition.x >= Chunk.size.x ||
        blockPosition.y < 0 || blockPosition.y >= Chunk.size.y ||
        blockPosition.z < 0 || blockPosition.z >= Chunk.size.z
    )
      BlockState(Block.AIR)
    else
      chunks(chunkPosition.x)(chunkPosition.y)(chunkPosition.z)
        .blockStates(blockPosition.x)(blockPosition.y)(blockPosition.z)
  }

  def getBlockState(x: Int, y: Int, z: Int): BlockState = getBlockState(
    x / Chunk.size.x,
    y / Chunk.size.y,
    z / Chunk.size.z,
    x % Chunk.size.x,
    y % Chunk.size.y,
    z % Chunk.size.z,
  )

  def getBlockState(chunkX: Int, chunkY: Int, chunkZ: Int, blockX: Int, blockY: Int, blockZ: Int): BlockState = {
    if (
      chunkX < 0 || chunkY < 0 || chunkZ < 0 ||
        chunkX >= numberOfChunks.x || chunkY >= numberOfChunks.y || chunkZ >= numberOfChunks.z ||
        blockX < 0 || blockY < 0 || blockZ < 0 ||
        blockX >= Chunk.size.x || blockY >= Chunk.size.y || blockZ >= Chunk.size.z
    )
      BlockState(Block.AIR)
    else
      chunks(chunkX)(chunkY)(chunkZ).blockStates(blockX)(blockY)(blockZ)
  }


  def generate(generator: WorldGenerator): Unit = {
    for (i <- chunks.indices; j <- chunks(0).indices; k <- chunks(0)(0).indices) {
      chunks(i)(j)(k) = Chunk()
      val chunk = chunks(i)(j)(k)
      chunk.generate(generator = generator, chunkPosition = Vector3i(i, j, k))
    }
  }
}
