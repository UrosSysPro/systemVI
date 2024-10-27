package com.systemvi.voxel.world.renderer

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.voxel.world.world2.BlockFace
import org.joml.Matrix4f

class BlockFaceRenderer {
  val view = Matrix4f()
  val projection = Matrix4f()

  val vertexArray = VertexArray()
  val arrayBuffer = ArrayBuffer()
  val elementBuffer = ElementsBuffer()

  vertexArray.bind()
  arrayBuffer.bind()
  elementBuffer.bind()
  arrayBuffer.setVertexAttributes(Array(
    VertexAttribute("position", 3)
  ))

  val shader: Shader = Shader.builder()
    .fragment("assets/examples/voxels/newBlockFaceRenderer/fragment.glsl")
    .vertex("assets/examples/voxels/newBlockFaceRenderer/vertex.glsl")
    .build()

  var faces = List.empty[BlockFace]

  def draw(face: BlockFace): Unit = faces = faces.prepended(face)

  def draw(faces: List[BlockFace]): Unit = this.faces ++= faces

  def view(mat:Matrix4f):Unit=view.set(mat)
  def projection(mat:Matrix4f):Unit=projection.set(mat)

  def flush(): Unit = {
    given Conversion[Int, Float] = (x: Int) => x.toFloat

    val vertexData = faces.flatMap { face =>
      val p = face.worldPosition
      Array[Float](
        p.x, p.y, p.z,
        p.x + 1, p.y, p.z,
        p.x, p.y + 1, p.z,
        p.x + 1, p.y + 1, p.z
      )
    }.toArray
    val elementData = faces.zipWithIndex.flatMap { (face, index) =>
      Array(index, index + 1, index + 2, index + 3)
    }.toArray

    vertexArray.bind()
    arrayBuffer.setData(vertexData)
    elementBuffer.setData(elementData)

    shader.use()
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(Primitive.TRIANGLE_STRIP,faces.length,ElementsDataType.UNSIGNED_INT,4)

    faces = List.empty[BlockFace]
  }
}
