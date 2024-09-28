package com.systemvi.triangle

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


    Breaks.breakable{
      while (true) {
        val startTime=System.nanoTime()
        Utils.clear(0, 0, 0, 0, Buffer.COLOR_BUFFER)
        window.pollEvents()
        if (window.shouldClose())Breaks.break()
        window.swapBuffers()
        val endTime=System.nanoTime()
        val frameTime=endTime-startTime
        System.out.printf("%10d",frameTime)
      }
    }
  }
}
