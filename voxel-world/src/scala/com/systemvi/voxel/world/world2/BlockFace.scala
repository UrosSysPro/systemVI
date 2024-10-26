package com.systemvi.voxel.world.world2

import com.systemvi.voxel.world.world.BlockState
import org.joml.Vector3i
enum BlockSide{
  case Top
  case Left
  case Right
  case Bottom
  case Front
  case Back
}
case class BlockFace(blockState:BlockState,worldPosition:Vector3i,side:BlockSide)
