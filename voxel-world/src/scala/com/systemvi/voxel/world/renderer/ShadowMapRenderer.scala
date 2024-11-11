package com.systemvi.voxel.world.renderer

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.texture.Texture.Repeat
import com.systemvi.engine.texture.{Format, FrameBuffer, Texture}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.voxel.world.world2.BlockFace
import org.joml.{Matrix4f, Vector3f}

case class Projection(aspect: Float, fov: Float, near: Float, far: Float)

case class Light(
                  position: Vector3f = Vector3f(),
                  rotation: Vector3f = Vector3f(),
                  color: Vector3f = Vector3f(1.0f),
                  attenuation: Vector3f = Vector3f(0, 0, 1),
                  projection: Projection = Projection(aspect = 800f / 600f, fov = Math.PI.toFloat / 3f, near = 0.1f, far = 100f)
                )

class ShadowMapRenderer(val width: Int, val height: Int, val light: Light) {
  val shadowMap: Texture = Texture.builder()
    .format(Format.DEPTH32)
    .width(width)
    .height(height)
    .verticalRepeat(Repeat.CLAMP_BORDER)
    .horizontalRepeat(Repeat.CLAMP_BORDER)
    .borderColor(Colors.black)
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
      List[Float](
        p.x, p.y, p.z, 0, 0, sideIndex,
        p.x, p.y, p.z, s, 0, sideIndex,
        p.x, p.y, p.z, 0, s, sideIndex,
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
  }

  def getView: Matrix4f = Matrix4f().rotateXYZ(
    -light.rotation.x,
    -light.rotation.y,
    -light.rotation.z
  ).translate(
    -light.position.x,
    -light.position.y,
    -light.position.z,
  )

  def getProjection: Matrix4f = Matrix4f().perspective(
    light.projection.fov,
    light.projection.aspect,
    light.projection.near,
    light.projection.far
  )

  def drawUploaded(): Unit = {
    val view = getView
    val projection = getProjection

    frameBuffer.begin()
    Utils.clearDepth()
    Utils.enableDepthTest()
    Utils.enableFaceCulling()
    vertexArray.bind()
    shader.use()
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.drawElements(
      Primitive.TRIANGLES,
      faces.length * 2,
      ElementsDataType.UNSIGNED_INT,
      3
    )
    Utils.disableFaceCulling()
    Utils.disableDepthTest()
    frameBuffer.end()
    Utils.viewport(0,0,800,600)
  }

  def clear(): Unit = {
    faces = List.empty[BlockFace]
    vertexArray.bind()
    arrayBuffer.setData(Array.empty[Float])
    elementBuffer.setData(Array.empty[Int])
  }
}
