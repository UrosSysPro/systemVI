package com.systemvi.ray_marching.sdf

import org.joml.*

trait SDF {
  def getValue(point: Vector3f): Float

  def toGlsl: String

  def translate(position: Vector3f) = Translate(this, position)

  def rotate(rotateXYZ: Vector3f) = Rotate(this, rotateXYZ)

  def scale(scale: Float) = Scale(this, scale)
}