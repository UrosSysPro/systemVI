package com.systemvi.triangle

import com.systemvi.engine.buffer.ArrayBuffer
import com.systemvi.engine.model.VertexAttribute
import com.systemvi.engine.shader.{Primitive, Shader}
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.{GLFW_CONTEXT_VERSION_MAJOR, GLFW_CONTEXT_VERSION_MINOR, GLFW_OPENGL_CORE_PROFILE, GLFW_OPENGL_PROFILE, glfwInit, glfwWindowHint}
import org.lwjgl.opengl.GL

import scala.util.control.Breaks

object Main {
  def main(args:Array[String]): Unit = {
    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)

    val window=new Window(800,600,"Triangle")

    val arrayBuffer=new ArrayBuffer()
    arrayBuffer.bind()
    arrayBuffer.setVertexAttributes(Array(
      new VertexAttribute("position",2)
    ))
    arrayBuffer.enableVertexAttribute(0)
    arrayBuffer.setData(Array(
          0, 0.5f,

      -0.5f,-0.5f,
      0.5f,-0.5f
    ))

    val shader=Shader.builder()
      .vertex("vertex.glsl")
      .fragment("fragment.glsl")
      .build()

    println(shader.getLog)
    println(shader.isCompiled)


    Breaks.breakable{
      while (true) {
        val startTime=System.nanoTime()

        window.pollEvents()
        if (window.shouldClose())Breaks.break()
        Utils.clear(Colors.green400, Buffer.COLOR_BUFFER)

        shader.use()
        arrayBuffer.bind()
        shader.drawArrays(Primitive.TRIANGLES,0,3)


        window.swapBuffers()
        val endTime=System.nanoTime()
        val frameTime=endTime-startTime
        val nano=frameTime%1000
        val micro=frameTime/1000%1000
        val milli=frameTime/1000000
        val fps=1000/(if milli==0 then 1 else milli)
        System.out.printf("\r %3d %3d %3d fps: %3d",milli,micro,nano,fps)
      }
    }
  }
}
