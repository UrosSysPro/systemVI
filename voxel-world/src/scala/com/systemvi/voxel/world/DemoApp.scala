package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.shader.Shader
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{World, WorldCache}
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
  var gbufferViewer:Shader=null
  
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


  }
}
