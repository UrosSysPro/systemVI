package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.buffer.VertexArray
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{World, WorldCache}
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11.{GL_NEAREST, GL_NEAREST_MIPMAP_LINEAR}

object DemoApp extends Game(3,3,60,800,600, "Demo Game"){

  val generator:WorldGenerator=PerlinWorldGenerator()
  val world:World=World()
  world.generate(generator)
  val worldCache:WorldCache=WorldCache(world)
  val blockRenderer:BlockFaceRenderer=BlockFaceRenderer()
  var controller:CameraController3=null
  var gbuffer:GBuffer=null
  var texture:Texture=null

  var viewerCamera:Camera3=null
  var emptyVertexArray:VertexArray=null
  var positionBufferViewer:Shader=null
  var uvBufferViewer:Shader=null
  
  override def setup(window: Window): Unit = {
    controller=CameraController3.builder()
      .window(window)
      .aspect(window.getWidth.toFloat/window.getHeight.toFloat)
      .far(1000)
      .speed(20)
      .build()
    setInputProcessor(controller)
    gbuffer=GBuffer(800,600)
    texture=Texture("assets/examples/minecraft/textures/diffuse.png")
    texture.setSamplerFilter(GL_NEAREST_MIPMAP_LINEAR, GL_NEAREST)
    for {
      col0 <- worldCache.chunkCache
      col1 <- col0
      chunkCache <- col1
    } blockRenderer.draw(chunkCache.blockFaces)
    blockRenderer.upload()


    viewerCamera=Camera3.builder2d()
      .position(400,300)
      .size(800,600)
      .build()
    positionBufferViewer=Shader.builder()
      .fragment("assets/examples/voxels/positionBufferViewer/fragment.glsl")
      .vertex("assets/examples/voxels/positionBufferViewer/vertex.glsl")
      .build()
    uvBufferViewer = Shader.builder()
      .fragment("assets/examples/voxels/uvBufferViewer/fragment.glsl")
      .vertex("assets/examples/voxels/uvBufferViewer/vertex.glsl")
      .build()
    emptyVertexArray=VertexArray()
  }

  override def loop(delta: Float): Unit ={
    controller.update(delta)

    gbuffer.begin()
    Utils.clear(Colors.black,Buffer.COLOR_BUFFER,Buffer.DEPTH_BUFFER)

    Utils.enableDepthTest()
    Utils.enableFaceCulling()
    blockRenderer.texture=texture
    blockRenderer.time=GLFW.glfwGetTime().toFloat
    blockRenderer.view(controller.camera.view)
    blockRenderer.projection(controller.camera.projection)
    blockRenderer.drawUploaded()
    Utils.disableFaceCulling()
    Utils.disableDepthTest()
    gbuffer.end()

    Utils.clear(Colors.green500,Buffer.COLOR_BUFFER)

    emptyVertexArray.bind()
    positionBufferViewer.use()
    gbuffer.position.bind(0)
    positionBufferViewer.setUniform("view",viewerCamera.view)
    positionBufferViewer.setUniform("projection",viewerCamera.projection)
    positionBufferViewer.setUniform("positionBuffer",0)
    positionBufferViewer.setUniform("rect",Vector4f(0,0,400,300))
    positionBufferViewer.drawArrays(Primitive.TRIANGLE_STRIP,0,4)

    uvBufferViewer.use()
    gbuffer.uv.bind(0)
    texture.bind(1)
    uvBufferViewer.setUniform("view", viewerCamera.view)
    uvBufferViewer.setUniform("projection", viewerCamera.projection)
    uvBufferViewer.setUniform("uvBuffer", 0)
    uvBufferViewer.setUniform("textureBuff", 1)
    uvBufferViewer.setUniform("rect", Vector4f(400, 0, 400, 300))
    uvBufferViewer.drawArrays(Primitive.TRIANGLE_STRIP, 0, 4)
  }
}
