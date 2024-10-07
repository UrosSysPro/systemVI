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
import org.joml.Vector2i
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL

import scala.util.control.Breaks.*

object Main extends Game(3, 3, 60, 800, 600, "Triangle") {
  var vertexArray: VertexArray = null
  var arrayBuffer: ArrayBuffer = null
  var elementsBuffer: ElementsBuffer = null
  var shader: Shader = null
  var camera: Camera3 = null
  val quads=new Vector2i(10,10)
  val gap:Float=10
  val size:Float=20

  override def setup(window: Window): Unit = {
    vertexArray = new VertexArray()
    arrayBuffer = new ArrayBuffer()
    elementsBuffer = new ElementsBuffer()

    {
      val width: Float = window.getWidth.toFloat
      val height: Float = window.getHeight.toFloat
      camera = Camera3.builder2d()
        .size(width, height)
        .position(width / 2, height / 2)
        .build()
    }
    vertexArray.bind()
    arrayBuffer.bind()
    elementsBuffer.bind()


    arrayBuffer.setVertexAttributes(Array(
      new VertexAttribute("position", 2)
    ))


    val width = 10
    val height = 10

    arrayBuffer.setData((for
      i <- 0 until quads.x
      j <- 0 until quads.y
      k <- 0 until 4
    yield
      Array[Float](
        i.toFloat*(size+gap) + (k % 2) * size,
        j.toFloat*(size+gap) + (k / 2) * size
      )).flatten.toArray
    )


    elementsBuffer.setData((for
      i <- 0 until quads.x
      j <- 0 until quads.y
    yield
      val index = (i + j * quads.x) * 4
      Array[Int](
        index + 0, index + 1, index + 2, index + 1, index + 2, index + 3
      )).flatten.toArray
    )


    shader = Shader.builder()
      .vertex("vertex.glsl")
      .fragment("fragment.glsl")
      .build()

    println(shader.getLog)
    println(shader.isCompiled)
  }

  override def loop(delta: Float): Unit = {
    val startTime = System.nanoTime()

    val window = getWindow
    window.pollEvents()
    if (window.shouldClose()) close()
    Utils.clear(Colors.green400, Buffer.COLOR_BUFFER)

    Utils.enableLines(2)
    shader.use()
    shader.setUniform("view", camera.view)
    shader.setUniform("projection", camera.projection)
    vertexArray.bind()
    shader.drawElements(Primitive.TRIANGLES, quads.x*quads.y*2, ElementsDataType.UNSIGNED_INT, 3)
    //    shader.drawArrays(Primitive.TRIANGLES, 0, 100)
    Utils.disableLines()

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
