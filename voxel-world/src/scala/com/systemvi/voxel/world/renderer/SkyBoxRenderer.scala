package com.systemvi.voxel.world.renderer

import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.texture.Texture.TextureType
import org.joml.{Matrix4f, Vector3f, Vector4f}


class SkyBoxMesh {
  private val vertexArray = VertexArray()
  private val arrayBuffer = ArrayBuffer()
  private val elementBuffer = ElementsBuffer()

  vertexArray.bind()
  arrayBuffer.bind()

  arrayBuffer.setVertexAttributes(Array(VertexAttribute("aPos", 3)))
  arrayBuffer.setData(Array(
    -1.0f,-1.0f,-1.0f, // triangle 1 : begin
    -1.0f,-1.0f, 1.0f,
    -1.0f, 1.0f, 1.0f, // triangle 1 : end
    1.0f, 1.0f,-1.0f, // triangle 2 : begin
    -1.0f,-1.0f,-1.0f,
    -1.0f, 1.0f,-1.0f, // triangle 2 : end
    1.0f,-1.0f, 1.0f,
    -1.0f,-1.0f,-1.0f,
    1.0f,-1.0f,-1.0f,

    1.0f, 1.0f,-1.0f,
    1.0f,-1.0f,-1.0f,
    -1.0f,-1.0f,-1.0f,

    -1.0f,-1.0f,-1.0f,
    -1.0f, 1.0f, 1.0f,
    -1.0f, 1.0f,-1.0f,

    1.0f,-1.0f, 1.0f,
    -1.0f,-1.0f, 1.0f,
    -1.0f,-1.0f,-1.0f,

    -1.0f, 1.0f, 1.0f,
    -1.0f,-1.0f, 1.0f,
    1.0f,-1.0f, 1.0f,

    1.0f, 1.0f, 1.0f,
    1.0f,-1.0f,-1.0f,
    1.0f, 1.0f,-1.0f,

    1.0f,-1.0f,-1.0f,
    1.0f, 1.0f, 1.0f,
    1.0f,-1.0f, 1.0f,

    1.0f, 1.0f, 1.0f,
    1.0f, 1.0f,-1.0f,
    -1.0f, 1.0f,-1.0f,

    1.0f, 1.0f, 1.0f,
    -1.0f, 1.0f,-1.0f,
    -1.0f, 1.0f, 1.0f,

    1.0f, 1.0f, 1.0f,
    -1.0f, 1.0f, 1.0f,
    1.0f,-1.0f, 1.0f
  ))

  def bind(): Unit = vertexArray.bind()
}

class SkyBoxRenderer {
  private val mesh:SkyBoxMesh=SkyBoxMesh()
  private val shader:Shader=Shader.builder()
    .fragment("assets/examples/voxels/skyboxRenderer/fragment.glsl")
    .vertex("assets/examples/voxels/skyboxRenderer/vertex.glsl")
    .build()
  private val texture:Texture=Texture.builder()
    .`type`(TextureType.CUBE_MAP)
    .cubeSides(
      "assets/examples/minecraft/cubemaps/random/negx.jpg",
      "assets/examples/minecraft/cubemaps/random/negy.jpg",
      "assets/examples/minecraft/cubemaps/random/negz.jpg",
      "assets/examples/minecraft/cubemaps/random/posx.jpg",
      "assets/examples/minecraft/cubemaps/random/posy.jpg",
      "assets/examples/minecraft/cubemaps/random/posz.jpg"
    )
    .build()
  private val view:Matrix4f=Matrix4f()
  private val projection:Matrix4f=Matrix4f()
  private val position:Vector3f=Vector3f()

  def view(mat:Matrix4f):Unit=view.set(mat)
  def projection(mat:Matrix4f):Unit=projection.set(mat)
  def position(v:Vector3f):Unit=position.set(v)

  def draw(): Unit = {
    shader.use()
    shader.setUniform("view",view)
    shader.setUniform("projection",projection)
    shader.setUniform("position",position)
    mesh.bind()
    texture.bind()
//    shader.setUniform("skybox",0)
    shader.drawArrays(Primitive.TRIANGLES,36)
  }
}
