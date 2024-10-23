package com.systemvi.shapes

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import org.joml.{Matrix4f, Vector2f, Vector3f, Vector4f}

trait Shape {
  def vertexData(): Array[Float]

  def elementData(): Array[Int]
}

case class Vertex(position: Vector2f, color: Vector4f)

class Triangle(val points: Array[Vertex]) extends Shape {
  override def vertexData(): Array[Float] = (
    for point <- points yield
      val p = point.position
      val c = point.color
      Array(p.x, p.y, c.x, c.y, c.z, c.w)
    ).flatten


  override def elementData(): Array[Int] = {
    Array(0, 1, 2)
  }
}

class ShapeRenderer {
  private val vertexArray=VertexArray()
  private val arrayBuffer=ArrayBuffer()
  private val elementBuffer=ElementsBuffer()
  private val shader=Shader.builder()
    .fragment("fragment.glsl")
    .vertex("vertex.glsl")
    .build()

  private var vertexData:Array[Float]=Array()
  private var elementsData:Array[Int]=Array()

  private val view=Matrix4f()
  private val projection=Matrix4f()

  vertexArray.bind()
  arrayBuffer.bind()
  arrayBuffer.setVertexAttributes(Array(
    VertexAttribute("position",2),
    VertexAttribute("color",4),
  ))
  elementBuffer.bind()

  def draw(shape: Shape): ShapeRenderer = {
    vertexData=shape.vertexData()
    elementsData=shape.elementData()
    this
  }

  def flush(): Unit = {
    shader.use()
    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementsData)
    shader.setUniform("view",view)
    shader.setUniform("projection",projection)
    shader.drawElements(Primitive.TRIANGLES,1,ElementsDataType.UNSIGNED_INT,3)
  }

  def view(m:Matrix4f):Unit=view.set(m)
  def projection(m:Matrix4f):Unit=projection.set(m)
}
