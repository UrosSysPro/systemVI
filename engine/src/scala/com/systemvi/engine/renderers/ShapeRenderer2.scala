package com.systemvi.engine.renderers

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.texture.{Texture, TextureRegion}
import com.systemvi.engine.ui.utils.data.Colors
import org.joml.{Matrix4f, Vector2f, Vector3f, Vector4f}

trait Shape {
  case class UV(top: Float, left: Float, bottom: Float, right: Float)

  case class Vertex(position: Vector2f, color: Vector4f, uv: Vector2f)

  def vertexData(): Array[Float]

  def elementData(index: Int): Array[Int]

  def getNumberOfVertices: Int
}

class Triangle(p0: Vector2f, p1: Vector2f, p2: Vector2f, color: Vector4f, transform: Matrix4f = Matrix4f().identity()) extends Shape {
  override def getNumberOfVertices: Int = 3

  override def vertexData(): Array[Float] = {
    val data: Array[Float] = Array.ofDim(16)
    transform.get(data)
    Array[Float](p0.x, p0.y, color.x, color.y, color.z, color.w, 0, 0) ++ data ++
      Array[Float](p1.x, p1.y, color.x, color.y, color.z, color.w, 0, 0) ++ data ++
      Array[Float](p2.x, p2.y, color.x, color.y, color.z, color.w, 0, 0) ++ data

  }

  override def elementData(index: Int): Array[Int] = Array[Int](index, index + 1, index + 2)
}

class Square(x: Float, y: Float, a: Float, color: Vector4f = Colors.white, region: TextureRegion = null, transform: Matrix4f = Matrix4f()) extends Shape {
  private val uv = if region != null then UV(region.getTop, region.getLeft, region.getBottom, region.getRight) else UV(-1, -1, -1, -1)
  private val vertices = Array(
    Vertex(Vector2f(x, y), color, Vector2f(uv.left, uv.top)),
    Vertex(Vector2f(x + a, y), color, Vector2f(uv.right, uv.top)),
    Vertex(Vector2f(x, y + a), color, Vector2f(uv.left, uv.bottom)),
    Vertex(Vector2f(x + a, y + a), color, Vector2f(uv.right, uv.bottom)),
  )

  override def getNumberOfVertices: Int = 4

  override def vertexData(): Array[Float] = {
    val data = Array.ofDim[Float](16)
    transform.get(data)
    vertices.flatMap(v =>
      Array(
        v.position.x, v.position.y, v.color.x, v.color.y, v.color.z, v.color.w, v.uv.x, v.uv.y
      ) ++ data
    )
  }

  override def elementData(index: Int): Array[Int] = Array(index, index + 1, index + 2, index + 1, index + 2, index + 3)
}

class ShapeRenderer2 {
  val view = Matrix4f()
  val projection = Matrix4f()
  var shapes = List.empty[Shape]

  val vertexArray = VertexArray()
  val arrayBuffer = ArrayBuffer()
  val elementBuffer = ElementsBuffer()

  vertexArray.bind()
  arrayBuffer.bind()
  elementBuffer.bind()
  arrayBuffer.setVertexAttributes(Array(
    VertexAttribute("position", 2),
    VertexAttribute("color", 4),
    VertexAttribute("uv", 2),
    VertexAttribute("col0", 4),
    VertexAttribute("col1", 4),
    VertexAttribute("col2", 4),
    VertexAttribute("col3", 4),
  ))

  var texture: Texture = null

  val shader = Shader.builder()
    .vertex("assets/renderer/shapeRenderer2/vertex.glsl")
    .fragment("assets/renderer/shapeRenderer2/fragment.glsl")
    .build()

  def view(mat: Matrix4f): Unit = view.set(mat)

  def projection(mat: Matrix4f): Unit = projection.set(mat)

  def draw(Shape2: Shape): Unit = shapes :+= Shape2

  def flush(): Unit = {
    val vertexData = shapes.flatMap(Shape2 => Shape2.vertexData()).toArray
    var index = 0
    val elementData = shapes.flatMap(Shape2 => {
      val array = Shape2.elementData(index)
      index += Shape2.getNumberOfVertices
      array
    }).toArray

    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementData)

    shader.use()
    texture.bind(0)
    shader.setUniform("tiles", 0)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(Primitive.TRIANGLES, elementData.length / 3, ElementsDataType.UNSIGNED_INT, 3)
    shapes = List.empty
  }
}
