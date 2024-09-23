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
  var windows:Seq[Window]=null
  var camera:Camera3=null
  var renderers:Seq[ShapeRenderer]=null

  override def setup(): Unit=
    windows=for(i<-0 until n)yield Window.builder()
      .size(width,height)
      .title(s"window $i")
      .build()
    camera=Camera3.builder2d()
      .size(width.toFloat,height.toFloat).position(width.toFloat/2,height.toFloat/2)
      .build()
    renderers=for(window<-windows)yield
      window.use()
      new ShapeRenderer()
    val window=windows.head
    println(window.version())
    println(window.renderer())
    println(window.extensions())
    println(window.vendor())
    println(window.shadingLanguageVersion())


  override def loop(delta: Float): Unit =
    for ((window,index)<-windows.zipWithIndex)
      window.use()
      window.pollEvents()
      if(window.shouldClose())close()
      Utils.clear(Colors.red200,Buffer.COLOR_BUFFER,Buffer.DEPTH_BUFFER)
      val renderer=renderers(index)
      renderer.setView(camera.view)
      renderer.setProjection(camera.projection)
      renderer.rect(0, 0, 100, 100, Colors.sky50)
      renderer.flush()
      window.swapBuffers()
  def main(args:Array[String]): Unit = Main.run()

