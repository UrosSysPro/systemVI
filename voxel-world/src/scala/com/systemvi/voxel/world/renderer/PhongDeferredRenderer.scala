package com.systemvi.voxel.world.renderer

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.camera.ProjectionType.Perspective
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.voxel.world.buffer.GBuffer
import org.joml.{Matrix4f, Vector3f, Vector4f}

case class ShadowMapLight(
                           position:Vector3f,
                           rotation:Vector3f,
                           view:Matrix4f,
                           projection: Matrix4f,
                           fov:Float,
                           near:Float,
                           aspect:Float,
                           far:Float,
                           bias:Float,
                           attenuation:Vector3f,
                           color:Vector3f
                         )

class PhongDeferredRenderer(
                           val gbuffer:GBuffer,
                           val diffuseMap:Texture,
                           val normalMap:Texture,
                           val skyboxTexture:Texture,
                           val shadowMap:Texture,
                           val viewerCamera:Camera3,
                           val worldCamera:Camera3,
                           val shadowMapLight: ShadowMapLight,
                           ) {

  val shader: Shader = Shader.builder()
    .fragment("assets/examples/voxels/combined_pbr/fragment.glsl")
    .vertex("assets/examples/voxels/combined_pbr/vertex.glsl")
    .build()

  def draw(rect:Vector4f): Unit = {
    val (far,near)= worldCamera.projectionType match
      case Perspective(fov,aspect,near,far) => (far,near)
      case _=>(0f,0f)
    shader.use()
    gbuffer.bind()
    diffuseMap.bind(6)
    normalMap.bind(7)
    skyboxTexture.bind(8)
    shadowMap.bind(9)
    shader.setUniform("view", viewerCamera.view)
    shader.setUniform("projection", viewerCamera.projection)
    shader.setUniform("positionBuffer", 0)
    shader.setUniform("normalBuffer", 1)
    shader.setUniform("tangentBuffer", 2)
    shader.setUniform("uvBuffer", 3)
    shader.setUniform("occlusionBuffer", 4)
    shader.setUniform("depthBuffer", 5)
    shader.setUniform("diffuseMap", 6)
    shader.setUniform("normalMap", 7)
    shader.setUniform("skybox", 8)
    shader.setUniform("shadowMap", 9)
    shader.setUniform("camera.position", worldCamera.position)
    shader.setUniform("camera.view", worldCamera.view)
    shader.setUniform("camera.projection", worldCamera.projection)
    shader.setUniform("camera.far", far)
    shader.setUniform("camera.near", near)
    shader.setUniform("rect", rect)
    shader.setUniform("far", far)
    shader.setUniform("near", near)
    shader.setUniform("shadowMapInfo.position", shadowMapLight.position)
    shader.setUniform("shadowMapInfo.rotation", shadowMapLight.rotation)
    shader.setUniform("shadowMapInfo.view", shadowMapLight.view)
    shader.setUniform("shadowMapInfo.projection", shadowMapLight.projection)
    shader.setUniform("shadowMapInfo.fov", shadowMapLight.fov)
    shader.setUniform("shadowMapInfo.aspect", shadowMapLight.aspect)
    shader.setUniform("shadowMapInfo.near", shadowMapLight.near)
    shader.setUniform("shadowMapInfo.far", shadowMapLight.far)
    shader.setUniform("shadowMapInfo.bias",shadowMapLight.bias)
    shader.setUniform("shadowMapInfo.attenuation", shadowMapLight.attenuation)
    shader.setUniform("shadowMapInfo.color", shadowMapLight.color)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
