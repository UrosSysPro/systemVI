package com.systemvi.voxel.world.renderer

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.voxel.world.world2.BlockFace
import org.joml.Matrix4f

class BlockFaceRenderer {
  var time: Float = 0
  private val view = Matrix4f()
  private val projection = Matrix4f()

  private val vertexArray = VertexArray()
  private val arrayBuffer = ArrayBuffer()
  private val elementBuffer = ElementsBuffer()

  vertexArray.bind()
  arrayBuffer.bind()
  elementBuffer.bind()
  arrayBuffer.setVertexAttributes(Array(
    VertexAttribute("worldPosition", 3),
    VertexAttribute("position", 2),
    VertexAttribute("sideIndex", 1),
  ))

  private val shader: Shader = Shader.builder()
    .fragment("assets/examples/voxels/newBlockFaceRenderer/fragment.glsl")
    .vertex("assets/examples/voxels/newBlockFaceRenderer/vertex.glsl")
    .build()

  private var faces = List.empty[BlockFace]

  def draw(face: BlockFace): Unit = faces = faces.prepended(face)

  def draw(faces: List[BlockFace]): Unit = this.faces ++= faces

  def view(mat: Matrix4f): Unit = view.set(mat)

  def projection(mat: Matrix4f): Unit = projection.set(mat)

  def flush(): Unit = {
    given Conversion[Int, Float] = (x: Int) => x.toFloat

    val verticesPerFace = 4
    val trianglesPerFace = 2

    val vertexData = faces.flatMap { face =>
      val p = face.worldPosition
      val sideIndex = face.side.index
      val s = 1f
      List[Float](
        p.x, p.y, p.z, 0f, 0f, sideIndex,
        p.x, p.y, p.z, s, 0f, sideIndex,
        p.x, p.y, p.z, 0f, s, sideIndex,
        p.x, p.y, p.z, s, s, sideIndex
      )
    }.toArray
    val elementData = faces.zipWithIndex.flatMap { (face, index) =>
      Array(
        index * verticesPerFace,
        index * verticesPerFace + 2,
        index * verticesPerFace + 1,

        index * verticesPerFace + 1,
        index * verticesPerFace + 2,
        index * verticesPerFace + 3,
      )
    }.toArray

    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementData)

    shader.use()
    shader.setUniform("time", time)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(
      Primitive.TRIANGLES,
      faces.length * trianglesPerFace,
      ElementsDataType.UNSIGNED_INT,
      3
    )

    faces = List.empty[BlockFace]
  }
}
