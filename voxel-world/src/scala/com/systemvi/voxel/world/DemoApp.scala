package com.systemvi.voxel.world

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.CameraController3
import com.systemvi.engine.window.Window
import com.systemvi.voxel.world.generators.{PerlinWorldGenerator, WorldGenerator}
import com.systemvi.voxel.world.renderer.BlockFaceRenderer
import com.systemvi.voxel.world.world2.{World, WorldCache}

object DemoApp extends Game(3,3,60,800,600, "Demo Game"){

  val generator:WorldGenerator=PerlinWorldGenerator()
  val world:World=World()
  val worldCache:WorldCache=WorldCache()
  val blockRenderer:BlockFaceRenderer=BlockFaceRenderer()
  var controller:CameraController3=null

  override def setup(window: Window): Unit = {
    controller=CameraController3.builder().build()
    setInputProcessor(controller)
  }

  override def loop(delta: Float): Unit ={
    controller.update(delta)
  }
}
