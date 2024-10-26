package com.systemvi.voxel.world.world2

import com.systemvi.voxel.world.world.{Block, BlockState}
import com.systemvi.voxel.world.world2
import org.joml.Vector3i

class ChunkCache(chunk: Chunk, chunkPosition: Vector3i) {

  var blockFaces: List[BlockFace] = List.empty

  for (i <- chunk.blockStates.indices; j <- chunk.blockStates(0).indices; k <- chunk.blockStates(0)(0).indices) {
    chunk.blockStates(i)(j)(k) match
      case state: BlockState if state.block == Block.AIR =>
      case state: BlockState =>
        //add left side
        if i - 1 < 0 || chunk.blockStates(i - 1)(j)(k).block == Block.AIR then
          blockFaces = BlockFace(state, Vector3i(i, j, k), BlockSide.Left) +: blockFaces
        //add right side
        if i + 1 >= Chunk.size.x || chunk.blockStates(i + 1)(j)(k).block == Block.AIR then
          blockFaces = BlockFace(state, Vector3i(i, j, k), BlockSide.Right) +: blockFaces
        //add top side
        if j + 1 >= Chunk.size.y || chunk.blockStates(i)(j + 1)(k).block == Block.AIR then
          blockFaces = BlockFace(state, Vector3i(i, j, k), BlockSide.Top) +: blockFaces
        //add bottom side
        if j - 1 < 0 || chunk.blockStates(i)(j - 1)(k).block == Block.AIR then
          blockFaces = BlockFace(state, Vector3i(i, j, k), BlockSide.Bottom) +: blockFaces
  }
  println(blockFaces.length)
}
