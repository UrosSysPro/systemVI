package com.systemvi.particle

import com.systemvi.engine.application.{Application, Game}
import com.systemvi.engine.buffer.{ArrayBuffer, ElementsBuffer, VertexArray}
import com.systemvi.engine.camera.{Camera3, CameraController3}
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{ElementsDataType, Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

import scala.util.control.Breaks.*

object Main extends Game(3, 3, 60,800,600,"Triangle") {
  var vertexArray: VertexArray = null
  var arrayBuffer: ArrayBuffer = null
  var elementsBuffer: ElementsBuffer = null
  var shader: Shader = null

  override def setup(window: Window): Unit = {
    vertexArray = new VertexArray()
    arrayBuffer = new ArrayBuffer()
    elementsBuffer = new ElementsBuffer()

    vertexArray.bind()
    arrayBuffer.bind()
    elementsBuffer.bind()

    arrayBuffer.setVertexAttributes(Array(
      new VertexAttribute("position", 2)
    ))

    val width=10
    val height=10
    val array=(for
      i<-0 until width
      j<-0 until height
      k<-0 until 4
    yield
      Array[Float](
        (i.toFloat+(k%2))*10,
        (j.toFloat+(k/2))*10
      )
    ).flatten.toArray




    shader = Shader.builder()
      .vertex("vertex.glsl")
      .fragment("fragment.glsl")
      .build()

    println(shader.getLog)
    println(shader.isCompiled)
  }

  override def loop(delta: Float): Unit = {
    val startTime = System.nanoTime()

    val window=getWindow
    window.pollEvents()
    if (window.shouldClose()) break
    Utils.clear(Colors.green400, Buffer.COLOR_BUFFER)

    //        Utils.enableLines(2)
    shader.use()
    vertexArray.bind()
    shader.drawElements(Primitive.TRIANGLES, 2, ElementsDataType.UNSIGNED_INT, 3)
    //        Utils.disableLines()

    val endTime = System.nanoTime()
    val frameTime = endTime - startTime
    val nano = frameTime % 1000
    val micro = frameTime / 1000 % 1000
    val milli = frameTime / 1000000
    val fps = 1000 / (if milli == 0 then 1 else milli)
    System.out.printf("\r %3d %3d %3d fps: %3d", milli, micro, nano, fps)
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}
