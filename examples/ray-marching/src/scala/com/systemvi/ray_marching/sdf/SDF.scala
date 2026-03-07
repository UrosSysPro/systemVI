package com.systemvi.ray_marching.sdf

import org.joml.Vector3f

trait SDF {
  def getValue(point: Vector3f): Float
//  def toGlsl: String
}

class Sphere(val radius:Float) extends SDF{
  override def getValue(point: Vector3f): Float = point.length() - radius
}

class Box(val halfSize:Vector3f) extends SDF{
  override def getValue(point: Vector3f): Float = {
    val q = Vector3f(point).absolute().sub(halfSize)
    Vector3f(q).max(Vector3f()).length() + Math.min(Math.max(q.x,Math.max(q.y,q.z)),0f)
  }
}

class Union(a:SDF,b:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = Math.min(a.getValue(point),b.getValue(point))
}

class Subtraction(a:SDF,b:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = Math.max(-a.getValue(point),b.getValue(point))
}

class Intersection(a:SDF,b:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = Math.max(a.getValue(point),b.getValue(point))
}
