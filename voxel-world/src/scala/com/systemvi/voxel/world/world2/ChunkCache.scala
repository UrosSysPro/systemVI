package com.systemvi.voxel.world.world2

import com.systemvi.voxel.world.world.{Block, BlockState}
import com.systemvi.voxel.world.world2
import org.joml.Vector3i

class ChunkCache(chunk: Chunk, chunkPosition: Vector3i) {

  var blockFaces: List[BlockFace] = List.empty
  private val w = Vector3i(chunkPosition.x * Chunk.size.x, chunkPosition.y * Chunk.size.y, chunkPosition.z * Chunk.size.z)
  for (i <- 0 until Chunk.size.x; j <- 0 until Chunk.size.y; k <- 0 until Chunk.size.z) {
    chunk.blockStates(i)(j)(k) match
      case state: BlockState if state.block == Block.AIR =>
      case state: BlockState =>
        //add left side
        if i - 1 < 0 || chunk.blockStates(i - 1)(j)(k).block == Block.AIR then
          blockFaces :+= BlockFace(state, Vector3i(w.x + i, w.y + j, w.z + k), BlockSide.Left)
        //add right side
        if i + 1 >= Chunk.size.x || chunk.blockStates(i + 1)(j)(k).block == Block.AIR then
          blockFaces :+= BlockFace(state, Vector3i(w.x + i, w.y + j, w.z + k), BlockSide.Right)
        //add bottom side
        if j - 1 < 0 || chunk.blockStates(i)(j - 1)(k).block == Block.AIR then
          blockFaces :+= BlockFace(state, Vector3i(w.x + i, w.y + j, w.z + k), BlockSide.Bottom)
        //add top side
        if j + 1 >= Chunk.size.y || chunk.blockStates(i)(j + 1)(k).block == Block.AIR then
          blockFaces :+= BlockFace(state, Vector3i(w.x + i, w.y + j, w.z + k), BlockSide.Top)
        //add front
        if k + 1 >= Chunk.size.z || chunk.blockStates(i)(j)(k + 1).block == Block.AIR then
          blockFaces :+= BlockFace(state, Vector3i(w.x + i, w.y + j, w.z + k), BlockSide.Front)
        //add back
        if k - 1 < 0 || chunk.blockStates(i)(j)(k - 1).block == Block.AIR then
          blockFaces :+= BlockFace(state, Vector3i(w.x + i, w.y + j, w.z + k), BlockSide.Back)
  }
  println(blockFaces.length)
}