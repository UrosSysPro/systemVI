package com.systemvi.engine.renderers


import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import org.joml.Matrix4f

class PolygonRenderer {
  private val vertexArray = VertexArray()
  private val arrayBuffer = ArrayBuffer()
  private val elementBuffer = ElementsBuffer()
  private val shader = Shader.builder()
    .fragment("assets/renderer/polygonRenderer/fragment.glsl")
    .vertex("assets/renderer/polygonRenderer/vertex.glsl")
    .build()

  private var vertexData: Array[Float] = Array()
  private var elementsData: Array[Int] = Array()

  private val view = Matrix4f()
  private val projection = Matrix4f()

  vertexArray.bind()
  arrayBuffer.bind()
  arrayBuffer.setVertexAttributes(Array(
    VertexAttribute("position", 2),
    VertexAttribute("color", 4),
    VertexAttribute("col0", 4),
    VertexAttribute("col1", 4),
    VertexAttribute("col2", 4),
    VertexAttribute("col3", 4),
  ))
  elementBuffer.bind()

  def draw(shape: Shape): PolygonRenderer = {
    val shapeVerticesData = shape.vertexData()
    val shapeElementsData = shape.elementData()
    elementsData ++= shapeElementsData.map(e => e + vertexData.length / 22)
    vertexData ++= shapeVerticesData
    this
  }

  def flush(): Unit = {
    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementsData)

    shader.use()
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(Primitive.TRIANGLES, elementsData.length / 3, ElementsDataType.UNSIGNED_INT, 3)

    vertexData = Array()
    elementsData = Array()
  }

  def view(m: Matrix4f): Unit = view.set(m)

  def projection(m: Matrix4f): Unit = projection.set(m)
}
