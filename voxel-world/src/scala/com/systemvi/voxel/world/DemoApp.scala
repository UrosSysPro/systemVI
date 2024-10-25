package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.buffer.GBuffer
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{World, WorldCache}

object DemoApp extends Game(3,3,60,800,600, "Demo Game"){

  val generator:WorldGenerator=PerlinWorldGenerator()
  val world:World=World(generator)
  val worldCache:WorldCache=WorldCache()
  val blockRenderer:BlockFaceRenderer=BlockFaceRenderer()
  var controller:CameraController3=null
  var gbuffer:GBuffer=null
  
  override def setup(window: Window): Unit = {
    controller=CameraController3.builder().build()
    setInputProcessor(controller)
    gbuffer=GBuffer(800,600)
  }

  override def loop(delta: Float): Unit ={
    controller.update(delta)
    Utils.clear(Colors.black,Buffer.COLOR_BUFFER)
  }
}
