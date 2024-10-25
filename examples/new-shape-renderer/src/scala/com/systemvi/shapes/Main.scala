package com.systemvi.shapes

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.{Polygon, PolygonRenderer}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.Vector2f

given Conversion[Int,Float] with
  override def apply(x: Int): Float = x.toFloat

object Main extends Game(3, 3, 60, 800, 600, "Shape Renderer") {

  var camera: Camera3 = null
  var renderer: PolygonRenderer = null
  var rotation=0f

  override def setup(window: Window): Unit = {
    val width = window.getWidth.toFloat
    val height = window.getHeight.toFloat
    camera = Camera3.builder2d()
      .position(width / 2, height / 2)
      .size(width, height)
      .build()
    renderer = PolygonRenderer()
  }

  override def loop(delta: Float): Unit = {
//    rotation+=delta

    Utils.clear(Colors.black, Buffer.COLOR_BUFFER)
    renderer.view(camera.view)
    renderer.projection(camera.projection)

    for(i<-0 until 16){
      val x=i%4*100+50
      val y=i/4*100+50
      renderer.draw(Polygon.regular(i+3,50,Colors.blue500,
        position = Vector2f(x,y),
        rotation=rotation
      ))
    }
//    Utils.enableLines(2)
    renderer.flush()
//    Utils.disableLines()
  }

  override def resize(width: Int, height: Int): Boolean = {
    camera.orthographic(-width/2,width/2,-height/2,height/2,0,1)
    camera.position.set(width/2,height/2,0)
    camera.update()
    true
  }

  def main(args: Array[String]): Unit = run()
}
