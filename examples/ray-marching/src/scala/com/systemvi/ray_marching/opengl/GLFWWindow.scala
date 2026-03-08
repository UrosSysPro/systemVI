package com.systemvi.ray_marching.opengl

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.{GL, GLCapabilities}
import org.lwjgl.opengl.GL11.{GL_RENDERER, GL_VENDOR, GL_VERSION, glGetString, glViewport}
import org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION
import org.lwjgl.opengl.GL33.*
import org.lwjgl.system.MemoryStack

import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
import scala.concurrent.ExecutionContext
import scala.util.Try

class GLFWWindow(
                      val id: Long,
                      private var _width:Int,
                      private var _height: Int,
                      private var _title: String,
                      val capabilities: GLCapabilities,
                     ) {
  private var (_x: Int, _y: Int) = {
    Try{
      val stack = MemoryStack.stackPush()
      val xBuffer = stack.ints(1)
      val yBuffer = stack.ints(1)
      glfwGetWindowPos(id,xBuffer,yBuffer)
      (xBuffer.get(0),yBuffer.get(0))
    }.getOrElse((0,0))
  }

  def x: Int = _x

  def y: Int = _y

  def width: Int = _width

  def height: Int = _height

  def title: String = _title

  def makeCurrent(): Unit = {
    glfwMakeContextCurrent(id)
    glViewport(0, 0, width, height)
  }

  def setPosition(x: Int, y: Int): Unit = {
    this._x = x
    this._y = y
    glfwSetWindowPos(this.id,x,y)
  }

  def setSize(width: Int, height: Int): Unit = {
    this._width = width
    this._height = height
    glfwSetWindowSize(this.id, this.width, this.height)
    glViewport(0, 0, this.width, this.height)
  }

  def setTitle(title: String): Unit = {
    this._title = title
    glfwSetWindowTitle(this.id, this.title)
  }

  def swapBuffers(): Unit = {
    glfwSwapBuffers(this.id)
  }

  def pollEvents(): Unit = {
    glfwPollEvents()
  }

  def shouldClose(): Boolean = {
    glfwWindowShouldClose(this.id)
  }

  def getRenderer: String = glGetString(GL_RENDERER)

  def getVendor: String = glGetString(GL_VENDOR)

  def getVersion: String = glGetString(GL_VERSION)

  def getGlslVersion: String = glGetString(GL_SHADING_LANGUAGE_VERSION)
}

object GLFWWindow {
  def make(context: GLFWContext, width: Int, height: Int, title: String): Resource[IO, GLFWWindow] = Resource.make{
    IO{
      val id = glfwCreateWindow(width, height, title, 0, 0)
      if(id==0){
        throw Exception("unable to create window")
      }
      glfwMakeContextCurrent(id)
      val capabilities = GL.createCapabilities
      glViewport(0, 0, width, height)
      GLFWWindow(
        id,
        width,
        height,
        title,
        capabilities
      )
    }.evalOn(context.ec)
  }{ window =>
    IO{
      glfwDestroyWindow(window.id)
    }.evalOn(context.ec)
  }
}
