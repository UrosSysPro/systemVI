package com.systemvi.engine.renderers

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.texture.{Texture, TextureRegion}
import org.joml.{Matrix4f, Vector2f, Vector3f, Vector4f}

trait Shape2 {
  def vertexData(): Array[Float]

  def elementData(index: Int): Array[Int]

  def getNumberOfVertices: Int
}

class Triangle(p0: Vector2f, p1: Vector2f, p2: Vector2f, color: Vector4f, transform: Matrix4f = Matrix4f().identity()) extends Shape2 {
  override def getNumberOfVertices: Int = 3

  override def vertexData(): Array[Float] = {
    val data: Array[Float] = Array.ofDim(16)
    transform.get(data)
    Array[Float](p0.x, p0.y, color.x, color.y, color.z, color.w,0,0) ++ data ++
      Array[Float](p1.x, p1.y, color.x, color.y, color.z, color.w,0,0) ++ data ++
      Array[Float](p2.x, p2.y, color.x, color.y, color.z, color.w,0,0) ++ data

  }

  override def elementData(index: Int): Array[Int] = Array[Int](index, index + 1, index + 2)
}

class Square(x: Float, y: Float, a: Float, color: Vector4f,region:TextureRegion, transform: Matrix4f = Matrix4f()) extends Shape2 {
  override def getNumberOfVertices: Int = 4

  override def vertexData(): Array[Float] = {
    val data = Array.ofDim[Float](16)
    transform.get(data)
    Array[Float](x, y, color.x, color.y, color.z, color.w,region.getLeft,region.getTop) ++ data ++
      Array[Float](x + a, y, color.x, color.y, color.z, color.w,region.getRight,region.getTop) ++ data ++
      Array[Float](x, y + a, color.x, color.y, color.z, color.w,region.getLeft,region.getBottom) ++ data ++
      Array[Float](x + a, y + a, color.x, color.y, color.z, color.w,region.getRight,region.getBottom) ++ data
  }

  override def elementData(index: Int): Array[Int] = Array(index, index + 1, index + 2, index + 1, index + 2, index + 3)
}

class Shape2Renderer2 {
  val view = Matrix4f()
  val projection = Matrix4f()
  var Shape2s = List.empty[Shape2]

  val vertexArray = VertexArray()
  val arrayBuffer = ArrayBuffer()
  val elementBuffer = ElementsBuffer()

  vertexArray.bind()
  arrayBuffer.bind()
  elementBuffer.bind()
  arrayBuffer.setVertexAttributes(Array(
    VertexAttribute("position", 2),
    VertexAttribute("color", 4),
    VertexAttribute("uv",2),
    VertexAttribute("col0", 4),
    VertexAttribute("col1", 4),
    VertexAttribute("col2", 4),
    VertexAttribute("col3", 4),
  ))

  var texture:Texture=null

  val shader = Shader.builder()
    .vertex("assets/renderer/shapeRenderer2/vertex.glsl")
    .fragment("assets/renderer/shapeRenderer2/fragment.glsl")
    .build()

  def view(mat: Matrix4f): Unit = view.set(mat)

  def projection(mat: Matrix4f): Unit = projection.set(mat)

  def draw(Shape2: Shape2): Unit = Shape2s :+= Shape2

  def flush(): Unit = {
    val vertexData = Shape2s.flatMap(Shape2 => Shape2.vertexData()).toArray
    var index = 0
    val elementData = Shape2s.flatMap(Shape2 => {
      val array = Shape2.elementData(index)
      index += Shape2.getNumberOfVertices
      array
    }).toArray

    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementData)

    shader.use()
    texture.bind(0)
    shader.setUniform("tiles",0)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(Primitive.TRIANGLES, elementData.length / 3, ElementsDataType.UNSIGNED_INT, 3)
    Shape2s = List.empty
  }
}
