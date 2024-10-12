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
import org.joml.{Vector2f, Vector2i}
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL


object Triangle extends Game(3, 3, 60, 800, 600, "Triangle") {
  var vertexArray: VertexArray = null
  var arrayBuffer: ArrayBuffer = null
  var elementsBuffer: ElementsBuffer = null
  var shader: Shader = null
  var camera: Camera3 = null

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

    arrayBuffer.setData(Array[Float](
      400,150,
      200,450,
      600,450
    ))

    elementsBuffer.setData(Array[Int](0,1,2))

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
    Utils.clear(Colors.black, Buffer.COLOR_BUFFER)

//    Utils.enableLines(2)
    shader.use()
    shader.setUniform("view", camera.view)
    shader.setUniform("projection", camera.projection)
    vertexArray.bind()
    shader.drawElements(Primitive.TRIANGLES, 1, ElementsDataType.UNSIGNED_INT, 3)
//    Utils.disableLines()

    val endTime = System.nanoTime()
    val frameTime = endTime - startTime
    val nano = frameTime % 1000
    val micro = frameTime / 1000 % 1000
    val milli = frameTime / 1000000
    val fps = 1000 / (if milli == 0 then 1 else milli)
    System.out.printf("\r %3d %3d %3d fps: %3d", milli, micro, nano, fps)
  }
}
