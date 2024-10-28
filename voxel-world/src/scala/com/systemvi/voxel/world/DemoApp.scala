package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.texture.Texture
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.{Buffer, Face}
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{World, WorldCache}
import org.lwjgl.glfw.GLFW

object DemoApp extends Game(3,3,60,800,600, "Demo Game"){

  val generator:WorldGenerator=PerlinWorldGenerator()
  val world:World=World()
  world.generate(generator)
  val worldCache:WorldCache=WorldCache(world)
  val blockRenderer:BlockFaceRenderer=BlockFaceRenderer()
  var controller:CameraController3=null
  var gbuffer:GBuffer=null
  var texture:Texture=null
  
  override def setup(window: Window): Unit = {
    controller=CameraController3.builder()
      .window(window)
      .aspect(window.getWidth.toFloat/window.getHeight.toFloat)
      .far(10000)
      .speed(20)
      .build()
    setInputProcessor(controller)
    gbuffer=GBuffer(800,600)
    texture=Texture("assets/examples/minecraft/textures/diffuse.png")
  }

  override def loop(delta: Float): Unit ={
    controller.update(delta)

    Utils.clear(Colors.black,Buffer.COLOR_BUFFER,Buffer.DEPTH_BUFFER)

    Utils.enableDepthTest()
    Utils.enableFaceCulling()
    blockRenderer.texture=texture
    blockRenderer.time=GLFW.glfwGetTime().toFloat
    blockRenderer.view(controller.camera.view)
    blockRenderer.projection(controller.camera.projection)
    for {
      col0 <- worldCache.chunkCache
      col1 <- col0
      chunkCache <- col1
    } blockRenderer.draw(chunkCache.blockFaces)
    blockRenderer.flush()
    Utils.disableFaceCulling()
    Utils.disableDepthTest()
  }
}
