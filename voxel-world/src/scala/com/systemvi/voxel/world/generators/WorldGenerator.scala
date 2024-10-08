package com.systemvi.voxel.world.generators

import org.joml.Vector3i

trait WorldGenerator {
  def get(block:Vector3i):Float
}
