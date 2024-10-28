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
  var cameraController: CameraController3 = null
  var shader: Shader = null

  override def setup(window: Window): Unit = {
    vertexArray = new VertexArray()
    arrayBuffer = new ArrayBuffer()
    elementsBuffer = new ElementsBuffer()
    cameraController = CameraController3.builder()
      .window(window)
      .camera(Camera3.builder3d()
        .build()
      ).build()
    setInputProcessor(cameraController)

    vertexArray.bind()
    arrayBuffer.bind()
    elementsBuffer.bind()

    arrayBuffer.setVertexAttributes(Array(
      new VertexAttribute("position", 2)
    ))
    arrayBuffer.setData(Array(
      0.5f, 0.5f,
      -0.5f, 0.5f,
      0.5f, -0.5f,
      -0.5f, -0.5f,
    ))
    elementsBuffer.setData(Array(
      0, 1, 2, 1, 2, 3
    ))

    shader = Shader.builder()
      .vertex("vertex.glsl")
      .fragment("fragment.glsl")
      .build()

    println(shader.getLog)
    println(shader.isCompiled)
  }

  override def loop(delta: Float): Unit = {
    cameraController.update(delta)
    val startTime = System.nanoTime()
    
    Utils.clear(Colors.green400, Buffer.COLOR_BUFFER)

    //        Utils.enableLines(2)
    shader.use()
    shader.setUniform("view",cameraController.camera.view)
    shader.setUniform("projection",cameraController.camera.projection)
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
    //    glfwInit()
    //    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    //    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    //    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    run()
  }
}
