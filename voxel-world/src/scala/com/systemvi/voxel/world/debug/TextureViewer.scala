package com.systemvi.voxel.world.debug

import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import org.joml.{Matrix4f, Vector3i, Vector4f}

trait TextureViewer() {
  def compileShader(fragment: String): Shader = Shader.builder()
    .fragment(fragment)
    .vertex("assets/examples/voxels/textureViewer/vertex.glsl")
    .build()
}

class PositionViewer(val worldSize:Vector3i) extends TextureViewer {
  val shader: Shader = compileShader("assets/examples/voxels/positionBufferViewer/fragment.glsl")
  val emptyVertexArray:VertexArray=VertexArray()
  def draw(texture:Texture,view:Matrix4f,projection:Matrix4f,rect:Vector4f): Unit = {
    emptyVertexArray.bind()
    shader.use()
    texture.bind(0)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.setUniform("worldSize", worldSize)
    shader.setUniform("positionBuffer", 0)
    shader.setUniform("rect", rect)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
