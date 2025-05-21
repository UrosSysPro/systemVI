package com.systemvi.voxel.world.world2

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
    val occlusions = Array(1f, 0.8f, 0.5f, 0f)
    val worldPosition = Vector3i(w).add(i, j, k)
    chunk.blockStates(i)(j)(k) match
      case state: BlockState if state.block == Block.AIR =>
      case state: BlockState =>
        //add left side
        if world.getBlockState(worldPosition.x - 1, worldPosition.y, worldPosition.z).block == Block.AIR then
          val top = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z)
          val bottom = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z)
          val front = world.getBlockState(worldPosition.x - 1, worldPosition.y, worldPosition.z + 1)
          val back = world.getBlockState(worldPosition.x - 1, worldPosition.y, worldPosition.z - 1)
          val topFront = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z + 1)
          val topBack = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z - 1)
          val bottomFront = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z + 1)
          val bottomBack = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z - 1)
          val x = Array(bottom, back, bottomBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val y = Array(bottom, front, bottomFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val z = Array(top, back, topBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val w = Array(top, front, topFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val occlusion = Vector4f(occlusions(x), occlusions(y), occlusions(z), occlusions(w))
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Left)
        //add right side
        if world.getBlockState(worldPosition.x + 1, worldPosition.y, worldPosition.z).block == Block.AIR then
          val top = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z)
          val bottom = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z)
          val front = world.getBlockState(worldPosition.x + 1, worldPosition.y, worldPosition.z + 1)
          val back = world.getBlockState(worldPosition.x + 1, worldPosition.y, worldPosition.z - 1)
          val topFront = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z + 1)
          val topBack = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z - 1)
          val bottomFront = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z + 1)
          val bottomBack = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z - 1)
          val x = Array(bottom, front, bottomFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val y = Array(bottom, back, bottomBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val z = Array(top, front, topFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val w = Array(top, back, topBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val occlusion = Vector4f(occlusions(x), occlusions(y), occlusions(z), occlusions(w))
          //          val occlusion = Vector4f(occlusions(x),1,1,1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Right)
        //add bottom side
        if world.getBlockState(worldPosition.x, worldPosition.y - 1, worldPosition.z).block == Block.AIR then
          val left = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z)
          val right = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z)
          val front = world.getBlockState(worldPosition.x, worldPosition.y - 1, worldPosition.z + 1)
          val back = world.getBlockState(worldPosition.x, worldPosition.y - 1, worldPosition.z - 1)
          val leftFront = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z + 1)
          val leftBack = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z - 1)
          val rightFront = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z + 1)
          val rightBack = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z - 1)
          val x = Array(left, front, leftFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val y = Array(right, front, rightFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val z = Array(left, back, leftBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val w = Array(right, back, rightBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val occlusion = Vector4f(occlusions(x), occlusions(y), occlusions(z), occlusions(w))
          //          val occlusion = Vector4f(0,1,1,1),1,1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Bottom)
        //add top side
        if world.getBlockState(worldPosition.x, worldPosition.y + 1, worldPosition.z).block == Block.AIR then
          val left = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z)
          val right = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z)
          val front = world.getBlockState(worldPosition.x, worldPosition.y + 1, worldPosition.z + 1)
          val back = world.getBlockState(worldPosition.x, worldPosition.y + 1, worldPosition.z - 1)
          val leftFront = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z + 1)
          val leftBack = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z - 1)
          val rightFront = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z + 1)
          val rightBack = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z - 1)
          val x = Array(left, front, leftFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val y = Array(right, front, rightFront).map(state => if state.block != Block.AIR then 1 else 0).sum
          val z = Array(left, back, leftBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val w = Array(right, back, rightBack).map(state => if state.block != Block.AIR then 1 else 0).sum
          val occlusion = Vector4f(occlusions(x), occlusions(y), occlusions(z), occlusions(w))
//          val occlusion = Vector4f(0,1,1,1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Top)
        //add front
        if world.getBlockState(worldPosition.x, worldPosition.y, worldPosition.z + 1).block == Block.AIR then
          val left        = world.getBlockState(worldPosition.x - 1, worldPosition.y, worldPosition.z + 1)
          val right       = world.getBlockState(worldPosition.x + 1, worldPosition.y , worldPosition.z + 1)
          val top         = world.getBlockState(worldPosition.x, worldPosition.y + 1, worldPosition.z + 1)
          val bottom      = world.getBlockState(worldPosition.x, worldPosition.y - 1, worldPosition.z + 1)
          val topLeft     = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z + 1)
          val topRight    = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z + 1)
          val bottomLeft  = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z + 1)
          val bottomRight = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z + 1)
          val x = Array(left, bottom, bottomLeft).map(state => if state.block != Block.AIR then 1 else 0).sum
          val y = Array(right, bottom, bottomRight).map(state => if state.block != Block.AIR then 1 else 0).sum
          val z = Array(left, top, topLeft).map(state => if state.block != Block.AIR then 1 else 0).sum
          val w = Array(right, top, topRight).map(state => if state.block != Block.AIR then 1 else 0).sum
          val occlusion = Vector4f(occlusions(x), occlusions(y), occlusions(z), occlusions(w))
//          val occlusion = Vector4f(0,1,1,1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Front)
        //add back
        if world.getBlockState(worldPosition.x, worldPosition.y, worldPosition.z - 1).block == Block.AIR then
          val left        = world.getBlockState(worldPosition.x - 1, worldPosition.y, worldPosition.z - 1)
          val right       = world.getBlockState(worldPosition.x + 1, worldPosition.y , worldPosition.z - 1)
          val top         = world.getBlockState(worldPosition.x, worldPosition.y + 1, worldPosition.z - 1)
          val bottom      = world.getBlockState(worldPosition.x, worldPosition.y - 1, worldPosition.z - 1)
          val topLeft     = world.getBlockState(worldPosition.x - 1, worldPosition.y + 1, worldPosition.z - 1)
          val topRight    = world.getBlockState(worldPosition.x + 1, worldPosition.y + 1, worldPosition.z - 1)
          val bottomLeft  = world.getBlockState(worldPosition.x - 1, worldPosition.y - 1, worldPosition.z - 1)
          val bottomRight = world.getBlockState(worldPosition.x + 1, worldPosition.y - 1, worldPosition.z - 1)
          val x = Array(right, bottom, bottomRight).map(state => if state.block != Block.AIR then 1 else 0).sum
          val y = Array(left, bottom, bottomLeft).map(state => if state.block != Block.AIR then 1 else 0).sum
          val z = Array(right, top, topRight).map(state => if state.block != Block.AIR then 1 else 0).sum
          val w = Array(left, top, topLeft).map(state => if state.block != Block.AIR then 1 else 0).sum
          val occlusion = Vector4f(occlusions(x), occlusions(y), occlusions(z), occlusions(w))
          //          val occlusion = Vector4f(0,1,1,1)
          blockFaces :+= BlockFace(state, worldPosition, occlusion, BlockSide.Back)
  }
}
