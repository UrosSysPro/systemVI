package com.systemvi.shapes

import org.joml.{Vector2f, Vector4f}

trait Shape:
  def vertexData(): Array[Float]
  def elementData(): Array[Int]

case class Vertex(position: Vector2f, color: Vector4f)

class Polygon(val points: Array[Vertex]) extends Shape:
  
  override def vertexData(): Array[Float] = (
    for point <- points yield
      val p = point.position
      val c = point.color
      Array(p.x, p.y, c.x, c.y, c.z, c.w)
    ).flatten

  override def elementData(): Array[Int] = 
    val n=points.length
    (for i<-0 until n-2 yield Array(0, i+1, i+2)).flatten.toArray
    
object Polygon:
  def regular(n:Int): Polygon = {
    
  }