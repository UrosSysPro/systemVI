package com.systemvi.engine.renderers

import org.joml.{Matrix4f, Vector2f, Vector4f}

trait Shape:
  def vertexData(): Array[Float]
  def elementData(): Array[Int]

case class Vertex(position: Vector2f, color: Vector4f, transform: Matrix4f)

class Polygon(val points: Array[Vertex]) extends Shape:

  private val data: Array[Float] = Array.ofDim[Float](16)

  override def vertexData(): Array[Float] = (
    for point <- points yield
      val p = point.position
      val c = point.color
      point.transform.get(data)
      Array(p.x, p.y, c.x, c.y, c.z, c.w)++data
    ).flatten

  override def elementData(): Array[Int] =
    val n = points.length
    (for i <- 0 until n - 2 yield Array(0, i + 1, i + 2)).flatten.toArray

object Polygon:
  def regular(
               n: Int,
               radius: Float,
               color: Vector4f,
               scale: Vector2f = Vector2f(1, 1),
               position: Vector2f = Vector2f(0, 0),
               rotation: Float = 0,
               transform: Matrix4f = null
             ): Polygon = Polygon((for (i <- 0 until n) yield {
    val a = (Math.PI * 2 / n * i).toFloat
    val x = Math.cos(a).toFloat * radius
    val y = Math.sin(a).toFloat * radius
    val t = if (transform != null)
      transform
    else
      Matrix4f()
        .translate(position.x, position.y, 0)
        .rotateZ(rotation)
        .scale(scale.x, scale.y, 1)
    Vertex(Vector2f(x, y), color, t)
  }).toArray)
