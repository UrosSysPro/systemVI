package com.systemvi.ray_marching.sdf

import org.joml.*

class Negative(a:SDF) extends SDF {
  override def getValue(point: Vector3f): Float = -a.getValue(point)

  override def toGlsl: String = s"-${a.toGlsl}"
}

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

class SmoothUnion(a:SDF, b:SDF, k: Float) extends SDF {
  override def getValue(point: Vector3f): Float = {
    val a1 = a.getValue(point)
    val b1 = b.getValue(point)
    var k1 = k
    k1 *= 4.0f
    val h = Math.max(k1 - Math.abs(a1 - b1), 0.0f)
    Math.min(a1, b1) - h * h * 0.25f / k1
  }

  override def toGlsl: String = s"smoothUnionSdf(${a.toGlsl},${b.toGlsl},$k)"
}

object SmoothUnion {
  def apply(sdfs: List[SDF], k: Float): SDF = {
    sdfs.drop(1).foldLeft(sdfs.head){(acc,sdf) => new SmoothUnion(acc,sdf,k)}
  }
}

//class SmoothDifference(a: SDF, b: SDF, k: Float) extends SDF {
//  override def getValue(point: Vector3f): Float = -SmoothUnion(a,Negative(b),k).getValue(point)
//
//  override def toGlsl: String = s"-smoothUnionSdf(${a.toGlsl},-${b.toGlsl},$k)"
//}
//
//class SmoothIntersection(a: SDF, b: SDF, k: Float) extends SDF {
//  override def getValue(point: Vector3f): Float = -SmoothUnion(Negative(a),Negative(b),k).getValue(point)
//
//  override def toGlsl: String = s"-smoothUnionSdf(-${a.toGlsl},-${b.toGlsl},$k)"
//}