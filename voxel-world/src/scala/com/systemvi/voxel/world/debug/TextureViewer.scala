package com.systemvi.voxel.world.debug

import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import org.joml.{Matrix4f, Vector2f, Vector3i, Vector4f}

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
class UVViewer extends TextureViewer {
  val shader: Shader = compileShader("assets/examples/voxels/uvBufferViewer/fragment.glsl")
  val emptyVertexArray:VertexArray=VertexArray()
  def draw(uvBuffer:Texture,diffuse: Texture,view:Matrix4f,projection:Matrix4f,rect:Vector4f): Unit = {
    emptyVertexArray.bind()
    shader.use()
    uvBuffer.bind(0)
    diffuse.bind(1)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.setUniform("uvBuffer", 0)
    shader.setUniform("textureBuff", 1)
    shader.setUniform("rect", rect)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}

class AOViewer extends TextureViewer {
  val shader: Shader = compileShader("assets/examples/voxels/aoViewer/fragment.glsl")
  val emptyVertexArray: VertexArray = VertexArray()

  def draw( texture: Texture, view: Matrix4f, projection: Matrix4f, rect: Vector4f): Unit = {
    emptyVertexArray.bind()
    shader.use()
    texture.bind(0)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.setUniform("textureBuff", 0)
    shader.setUniform("rect", rect)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
class DepthViewer extends TextureViewer {
  val shader: Shader = compileShader("assets/examples/voxels/depthBufferViewer/fragment.glsl")
  val emptyVertexArray:VertexArray=VertexArray()
  def draw(depthBuffer:Texture,near:Float,far:Float,view:Matrix4f,projection:Matrix4f,rect:Vector4f): Unit = {
    emptyVertexArray.bind()
    shader.use()
    depthBuffer.bind(0)
    shader.setUniform("near", near)
    shader.setUniform("far", far)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.setUniform("depthBuffer", 0)
    shader.setUniform("rect", rect)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}

class TBNViewer extends TextureViewer {
  val shader: Shader = compileShader("assets/examples/voxels/tbnBufferViewer/fragment.glsl")
  val emptyVertexArray:VertexArray=VertexArray()
  def draw(texture:Texture,view:Matrix4f,projection:Matrix4f,rect:Vector4f): Unit = {
    emptyVertexArray.bind()
    shader.use()
    texture.bind(0)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.setUniform("textureBuff", 0)
    shader.setUniform("rect", rect)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}

class ToneMapper extends TextureViewer{
  val shader:Shader=compileShader("assets/examples/voxels/toneMapper/fragment.glsl")
  val emptyVertexArray: VertexArray = VertexArray()
  def draw(texture: Texture, size:Vector2f, view: Matrix4f, projection: Matrix4f, rect: Vector4f): Unit = {
    emptyVertexArray.bind()
    shader.use()
    texture.bind(0)
    shader.setUniform("size",size)
    shader.setUniform("view", view)
    shader.setUniform("projection", projection)
    shader.setUniform("textureBuff", 0)
    shader.setUniform("rect", rect)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}