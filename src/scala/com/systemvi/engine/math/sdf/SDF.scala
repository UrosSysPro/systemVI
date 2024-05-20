package com.systemvi.engine.math.sdf

import org.joml.Vector3f

object Translate{
  def apply(position:Vector3f,translate:Vector3f):Vector3f = new Vector3f(position).sub(translate)
}
object Sphere{
  def apply(position:Vector3f, translate:Vector3f=new Vector3f(0), r:Float=1): Float = Translate(position,translate).length()-r
}
object Union{
  def apply(d:Float*): Float = d.foldLeft(Float.MaxValue){(acc,d)=>Math.min(acc,d)}
}
object Box{
  def apply(position:Vector3f, translate:Vector3f, rotation:Vector3f, scale:Vector3f): Float = 0
}
object Plane{
  def apply(position:Vector3f,translate:Vector3f):Float=Translate(position,translate).y
}