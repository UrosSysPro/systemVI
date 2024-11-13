package com.systemvi.voxel.world.renderer

import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.camera.ProjectionType.Perspective
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.voxel.world.buffer.GBuffer
import org.joml.Vector4f


class PBRDeferredRenderer(
                           val gbuffer:GBuffer,
                           val diffuseMap:Texture,
                           val normalMap:Texture,
                           val skyboxTexture:Texture,
                           val shadowMap:Texture,
                           val playerCamera:Camera3,
                           val viewerCamera:Camera3,
                           val shadowMapRenderer: ShadowMapRenderer,
                         ) {
  val shader:Shader=Shader.builder()
    .vertex("assets/examples/voxels/textureViewer/vertex.glsl")
    .fragment("assets/examples/voxels/combined_pbr/fragment.glsl")
    .build()

  def draw(rect:Vector4f): Unit = {
    val (far, near) = playerCamera.projectionType match
      case Perspective(fov, aspect, near, far) => (far, near)
      case _ => (0f, 0f)
    val light=shadowMapRenderer.light
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
    shader.setUniform("camera.position", playerCamera.position)
    shader.setUniform("camera.view", playerCamera.view)
    shader.setUniform("camera.projection", playerCamera.projection)
    shader.setUniform("camera.far", far)
    shader.setUniform("camera.near", near)
    shader.setUniform("rect", rect)
    shader.setUniform("far", far)
    shader.setUniform("near", near)
    shader.setUniform("shadowMapInfo.position", light.position)
    shader.setUniform("shadowMapInfo.rotation", light.rotation)
    shader.setUniform("shadowMapInfo.view", shadowMapRenderer.getView)
    shader.setUniform("shadowMapInfo.projection", shadowMapRenderer.getProjection)
    shader.setUniform("shadowMapInfo.fov", light.projection.fov)
    shader.setUniform("shadowMapInfo.aspect", light.projection.aspect)
    shader.setUniform("shadowMapInfo.near", light.projection.near)
    shader.setUniform("shadowMapInfo.far", light.projection.far)
    shader.setUniform("shadowMapInfo.bias", 0.00001f)
    shader.setUniform("shadowMapInfo.attenuation", light.attenuation)
    shader.setUniform("shadowMapInfo.color", light.color)
    shader.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
