package com.systemvi.shapes

import com.systemvi.engine.application.Game
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.joml.Vector2f

object Main extends Game(3, 3, 60, 800, 600, "Shape Renderer") {

  var camera: Camera3 = null
  var renderer: ShapeRenderer = null

  override def setup(window: Window): Unit = {
    val width = window.getWidth.toFloat
    val height = window.getHeight.toFloat
    camera = Camera3.builder2d()
      .position(width / 2, height / 2)
      .size(width, height)
      .build()
    renderer = ShapeRenderer()
  }

  override def loop(delta: Float): Unit = {
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER)
    renderer.view(camera.view)
    renderer.projection(camera.projection)

    renderer.draw(Polygon((for (i <- 0 until 5) yield {
      val a: Float = (Math.PI * i * 2 / 5).toFloat
      val x = Math.cos(a).toFloat*100+300
      val y = Math.sin(a).toFloat*100+200
      Vertex(position = Vector2f(x, y), color = Colors.blue500)
    }).toArray))
    renderer.flush()
  }

  def main(args: Array[String]): Unit = run()
}
