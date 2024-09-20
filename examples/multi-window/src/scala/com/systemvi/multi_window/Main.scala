package com.systemvi.multi_window

import com.systemvi.engine.application.{Application, Game}
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.ShapeRenderer
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window

import scala.language.postfixOps

object Main extends Application(3,3,60):

  val n=4
  val width=400
  val height=300
  var windows:Array[Window]=null
  var camera:Camera3=null

  override def setup(): Unit=
    windows=(0 until n).map{i=>new Window(width,height,s"Window $i")}.toArray
    camera=Camera3.builder2d()
      .size(width.toFloat,height.toFloat).position(width.toFloat/2,height.toFloat/2)
      .build()


  override def loop(delta: Float): Unit =
    for (window<-windows)
      window.use()
      window.pollEvents()
      if(window.shouldClose())close()
      window.swapBuffers()


  def main(args: Array[String]): Unit =
    run()

