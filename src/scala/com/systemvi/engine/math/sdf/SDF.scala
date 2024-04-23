package com.systemvi.engine.math.sdf

import org.joml.Vector3f

class SDF {
  def sphere(p:Vector3f, s: Float): Float = p.length()- s

  def box(p: Vector3f, b: Vector3f): Float = {
    val q = new Vector3f(p.absolute()).sub(b)
    q.max(new Vector3f(0)).length()+Math.min(Math.max(q.x,Math.max(q.y,q.z)),0)
  }
  def union(d0:Float,d1:Float): Unit = Math.min(d0,d1)
}
