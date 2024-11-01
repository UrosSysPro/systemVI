package com.systemvi.voxel.world.world2

import com.systemvi.voxel.world.world.{Block, BlockState}
import com.systemvi.voxel.world.world2
import org.joml.{Vector3i, Vector4f}

class ChunkCache(world: World, chunk: Chunk, chunkPosition: Vector3i) {

  var blockFaces: List[BlockFace] = List.empty
  private val w = Vector3i(
    chunkPosition.x * Chunk.size.x,
    chunkPosition.y * Chunk.size.y,
    chunkPosition.z * Chunk.size.z
  )
  for (i <- 0 until Chunk.size.x; j <- 0 until Chunk.size.y; k <- 0 until Chunk.size.z) {
    val worldPosition = Vector3i(w).add(i, j, k)
    chunk.blockStates(i)(j)(k) match
      case state: BlockState if state.block == Block.AIR =>
      case state: BlockState =>
        //add left side
        if world.getBlockState(worldPosition.x - 1, worldPosition.y, worldPosition.z).block == Block.AIR then
          val occlusion = Vector4f(1, 1, 1, 1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Left)
        //add right side
        if world.getBlockState(worldPosition.x + 1, worldPosition.y, worldPosition.z).block == Block.AIR then
          val occlusion = Vector4f(1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Right)
        //add bottom side
        if world.getBlockState(worldPosition.x, worldPosition.y - 1, worldPosition.z).block == Block.AIR then
          val occlusion = Vector4f(1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Bottom)
        //add top side
        if world.getBlockState(worldPosition.x, worldPosition.y + 1, worldPosition.z).block == Block.AIR then
          val occlusion = Vector4f(1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Top)
        //add front
        if world.getBlockState(worldPosition.x, worldPosition.y, worldPosition.z + 1).block == Block.AIR then
          val occlusion = Vector4f(1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Front)
        //add back
        if world.getBlockState(worldPosition.x, worldPosition.y, worldPosition.z - 1).block == Block.AIR then
          val occlusion = Vector4f(1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Back)
  }
}
