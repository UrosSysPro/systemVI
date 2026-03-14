package com.systemvi.ray_marching.sdf

import org.joml.*

class Sphere(val radius:Float) extends SDF{
  override def getValue(point: Vector3f): Float = point.length() - radius

  override def toGlsl: String = s"sphereSdf($radius,p)"
}

class Box(val halfSize:Vector3f) extends SDF{
  override def getValue(point: Vector3f): Float = {
    val q = Vector3f(point).absolute().sub(halfSize)
    Vector3f(q).max(Vector3f()).length() + Math.min(Math.max(q.x,Math.max(q.y,q.z)),0f)
  }

  override def toGlsl: String = s"boxSdf(vec3(${halfSize.x},${halfSize.y},${halfSize.z}),p)"
}
