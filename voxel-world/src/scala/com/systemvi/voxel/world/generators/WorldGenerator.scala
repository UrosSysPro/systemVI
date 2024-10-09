package com.systemvi.voxel.world.generators

import com.systemvi.voxel.world.world.Block
import org.joml.Vector3i

trait WorldGenerator {
  def get(block:Vector3i):Block
}
