package com.systemvi.multi_window

import com.systemvi.engine.application.{Application, Game}
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.ShapeRenderer
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

object Main extends Application(3,3,60){

  var window1:Window=null
  var window2:Window=null
  var shapeRenderer:ShapeRenderer=null
  var camera:Camera3=null

  override def setup(): Unit = {
    window1=new Window(800,600,"window 1")
//    window2=new Window(800,600,"window 2")
    camera=Camera3.builder2d()
      .size(800,600)
//      .position(400,300)
      .scale(1,-1)
      .build()
    shapeRenderer=new ShapeRenderer();
    shapeRenderer.setView(camera.view)
    shapeRenderer.setProjection(camera.projection)
  }

  override def loop(delta: Float): Unit = {
    if(window1.shouldClose()){
      close()
    }
//    window1.use()
    window1.pollEvents()
    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
    shapeRenderer.rect(0,0,200,300,Colors.blue500)
    shapeRenderer.flush()
    window1.swapBuffers()
//    window2.use()
//    window2.pollEvents()
//    Utils.clear(0,0,0,0,Buffer.COLOR_BUFFER)
//    shapeRenderer.rect(100,100,200,300,Colors.blue500)
//    shapeRenderer.flush()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}