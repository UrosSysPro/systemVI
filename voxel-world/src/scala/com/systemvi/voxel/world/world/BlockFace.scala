package com.systemvi.voxel.world.world

import org.joml.{Vector3i, Vector4f}

enum BlockSide(val index:Int):
  case Left extends BlockSide(0)
  case Right extends BlockSide(1)
  case Top extends BlockSide(2)
  case Bottom extends BlockSide(3)
  case Front extends BlockSide(4)
  case Back extends BlockSide(5)

case class BlockFace(blockState:BlockState, worldPosition:Vector3i, occlusion:Vector4f, side:BlockSide)
