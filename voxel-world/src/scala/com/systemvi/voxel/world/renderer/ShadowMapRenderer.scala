package com.systemvi.voxel.world.renderer

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.voxel.world.world2.{BlockFace, BlockSide}
import org.joml.{Matrix4f, Vector3f, Vector4f}

case class Light(
                  position:Vector3f=Vector3f(),
                  rotation:Vector3f=Vector3f(),
                  color:Vector4f=Colors.white,
                  attenuation:Vector3f=Vector3f(0,0,1)
                )

class ShadowMapRenderer(width: Int, height: Int,val light:Light) {
  val shadowMap: Texture = Texture.builder()
    .format(Format.DEPTH32)
    .width(width)
    .height(height)
    .build()
  val frameBuffer: FrameBuffer = FrameBuffer.builder()
    .depth(shadowMap)
    .build()

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
    .fragment("assets/examples/voxels/shadowMapRenderer/fragment.glsl")
    .vertex("assets/examples/voxels/shadowMapRenderer/vertex.glsl")
    .build()

  private var faces = List.empty[BlockFace]


  def draw(face: BlockFace): Unit = faces = faces.prepended(face)

  def draw(faces: List[BlockFace]): Unit = this.faces ++= faces

//  def view(mat: Matrix4f): Unit = view.set(mat)

//  def projection(mat: Matrix4f): Unit = projection.set(mat)

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
      List[Float](
        p.x, p.y, p.z, sideIndex,
        p.x, p.y, p.z, sideIndex,
        p.x, p.y, p.z, sideIndex,
        p.x, p.y, p.z, sideIndex
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
//    shader.setUniform("view", view)
//    shader.setUniform("projection", projection)
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
