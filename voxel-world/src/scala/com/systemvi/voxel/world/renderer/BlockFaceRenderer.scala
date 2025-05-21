package com.systemvi.voxel.world.renderer

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.texture.FrameBuffer.Attachment
import com.systemvi.voxel.world.world.{BlockFace, BlockSide}
import org.joml.{Matrix4f, Vector4f}

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
    VertexAttribute("uv", 2),
    VertexAttribute("occlusion", 1),
    VertexAttribute("sideIndex", 1),
  ))

  private val shader: Shader = Shader.builder()
    .fragment("assets/examples/voxels/newBlockFaceRenderer/fragment.glsl")
    .vertex("assets/examples/voxels/newBlockFaceRenderer/vertex.glsl")
    .build()

  private var faces = List.empty[BlockFace]
  
  def setRenderTargets(attachments: Array[Attachment]): Unit = shader.setRenderTarget(attachments)
  

  def draw(face: BlockFace): Unit = faces = faces.prepended(face)

  def draw(faces: List[BlockFace]): Unit = this.faces ++= faces

  def view(mat: Matrix4f): Unit = view.set(mat)

  def projection(mat: Matrix4f): Unit = projection.set(mat)

  def flush(): Unit = {
    upload()
    drawUploaded()
    clear()
  }

  def upload(): Unit = {
    given Conversion[Int, Float] = (x: Int) => x.toFloat

    val verticesPerFace = 4
    val trianglesPerFace = 2

    val vertexData = faces.flatMap { face =>
      val p = face.worldPosition
      val sideIndex = face.side.index
      val s = 1f
      val region = face.side match
        case BlockSide.Back => face.blockState.block.back
        case BlockSide.Front => face.blockState.block.front
        case BlockSide.Left => face.blockState.block.left
        case BlockSide.Right => face.blockState.block.right
        case BlockSide.Top => face.blockState.block.top
        case BlockSide.Bottom => face.blockState.block.bottom
      val occlusion = face.occlusion
      val o = occlusion
      List[Float](
        p.x, p.y, p.z, 0, 0, region.getLeft, region.getBottom, o.x, sideIndex,
        p.x, p.y, p.z, s, 0, region.getRight, region.getBottom, o.y, sideIndex,
        p.x, p.y, p.z, 0, s, region.getLeft, region.getTop, o.z, sideIndex,
        p.x, p.y, p.z, s, s, region.getRight, region.getTop, o.w, sideIndex
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
  }

  def drawUploaded(): Unit = {
    vertexArray.bind()

    shader.use()
    shader.setUniform("time", time)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(
      Primitive.TRIANGLES,
      faces.length * 2,
      ElementsDataType.UNSIGNED_INT,
      3
    )
  }

  def clear(): Unit = {
    faces = List.empty[BlockFace]
    vertexArray.bind()
    arrayBuffer.setData(Array.empty[Float])
    elementBuffer.setData(Array.empty[Int])
  }
}
