package com.systemvi.ray_marching.sdf

import org.joml.*

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
