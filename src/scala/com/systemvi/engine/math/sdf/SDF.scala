package com.systemvi.engine.math.sdf

import org.joml.{Matrix4f, Vector3f, Vector4f}

object Translate{
  def apply(position:Vector3f,translate:Vector3f):Vector3f = new Vector3f(position).sub(translate)
}
object Rotate{
  def apply(position:Vector3f,rotation:Vector3f):Vector3f = {
    val mat=new Matrix4f().setRotationXYZ(-rotation.x,-rotation.y,-rotation.z)
    val helper=new Vector4f(position.x,position.y,position.z,1).mul(mat)
    new Vector3f(helper.x,helper.y,helper.z)
  }
}
object Sphere{
  def apply(position:Vector3f, translate:Vector3f=new Vector3f(0), r:Float=1): Float = Translate(position,translate).length()-r
}
object Union{
  def apply(d:Float*): Float = d.foldLeft(Float.MaxValue){(acc,d)=>Math.min(acc,d)}
}
object Box{
  def apply(position:Vector3f, size:Vector3f, translate:Vector3f=new Vector3f(), rotation:Vector3f=new Vector3f()): Float = {
    val q=new Vector3f(Rotate(Translate(position,translate),rotation)).absolute().sub(size)
    new Vector3f(Math.max(q.x,0),Math.max(q.y,0),Math.max(q.z,0)).length()+Math.min(Math.max(q.x,Math.max(q.y,q.z)),0)
  }
}
object Plane{
  def apply(position:Vector3f,translate:Vector3f=new Vector3f()):Float=Translate(position,translate).y
}