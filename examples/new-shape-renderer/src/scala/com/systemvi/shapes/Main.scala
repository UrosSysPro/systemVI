package com.systemvi.shapes

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

object Main extends Game(3,3,60,800,600,"Polygon Renderer"){

  var shapeRenderer:ShapeRenderer=null
  var camera:Camera3=null

  override def setup(window: Window): Unit = {
    val width=window.getWidth.toFloat
    val height=window.getHeight.toFloat
    camera=Camera3.builder2d()
      .position(width/2,height/2)
      .size(width, height)
      .build()
    shapeRenderer=ShapeRenderer()
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black,Buffer.COLOR_BUFFER)
    shapeRenderer.view(camera.view)
    shapeRenderer.projection(camera.projection)
    
//    shapeRenderer.draw(Triangle())
//    shapeRenderer.draw(Square())
//    shapeRenderer.draw(Line())
    
    shapeRenderer.flush()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}