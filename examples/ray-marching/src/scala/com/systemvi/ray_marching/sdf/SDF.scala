package com.systemvi.ray_marching.sdf

import org.joml.{Matrix4f, Vector3f, Vector4f}

trait SDF {
  def getValue(point: Vector3f): Float
  def toGlsl: String
  def translate(position: Vector3f) = Translate(this,position)
  def rotate(rotateXYZ: Vector3f) = Rotate(this,rotateXYZ)
  def scale(scale: Float) = Scale(this,scale)
}

//primitives
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

//operations
class Union(a:SDF,b:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = Math.min(a.getValue(point),b.getValue(point))

  override def toGlsl: String = s"unionSdf(${a.toGlsl},${b.toGlsl})"
}
object Union {
  def apply(sdfs: SDF*): SDF = {
    sdfs.drop(1).foldLeft(sdfs.head){(acc,sdf) => new Union(acc,sdf)}
  }
}

class Difference(a:SDF,b:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = Math.max(-a.getValue(point),b.getValue(point))

  override def toGlsl: String = s"differenceSdf(${a.toGlsl},${b.toGlsl})"
}

class Intersection(a:SDF,b:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = Math.max(a.getValue(point),b.getValue(point))

  override def toGlsl: String = s"intersectionSdf(${a.toGlsl},${b.toGlsl})"
}

// transforms
class Translate(sdf: SDF, position: Vector3f) extends SDF {
  override def getValue(point: Vector3f): Float = sdf.getValue(Vector3f(point).sub(position))

  override def toGlsl: String = sdf.toGlsl.replace(",p", s",translateSdf(vec3(${position.x},${position.y},${position.z}),p)")
}

class Rotate(sdf: SDF, rotationXYZ: Vector3f) extends SDF {
  override def getValue(point: Vector3f): Float = {
    val mat = Matrix4f().identity()
      .rotateXYZ(
        -rotationXYZ.x,
        -rotationXYZ.y,
        -rotationXYZ.z,
      )
    val p = Vector4f(point,1f).mul(mat)
    sdf.getValue(Vector3f(p.x,p.y,p.z))
  }

  override def toGlsl: String = sdf.toGlsl.replace(",p",s"rotateSdf(vec3(${rotationXYZ.x},${rotationXYZ.y},${rotationXYZ.z}),p)")
}

class Scale(sdf: SDF, scale: Float) extends SDF {
  override def getValue(point: Vector3f): Float = sdf.getValue(Vector3f(point).div(scale))*scale

  override def toGlsl: String = s"${sdf.toGlsl.replace(",p",s",p/$scale")}*$scale"
}