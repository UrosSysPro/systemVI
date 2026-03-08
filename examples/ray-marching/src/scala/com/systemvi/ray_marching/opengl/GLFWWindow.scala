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

import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
import scala.concurrent.ExecutionContext

class GLFWWindow(
                      val id: Long,
                      var width:Int,
                      var height: Int,
                      var title: String,
                      val capabilities: GLCapabilities,
                     ) {
  def makeCurrent(): Unit = {
    glfwMakeContextCurrent(id)
    glViewport(0, 0, width, height)
  }

  def setPosition(x: Int, y: Int)(): Unit = {
    glfwSetWindowPos(this.id,x,y)
  }

  def setSize(width: Int, height: Int): Unit = {
    this.width = width
    this.height = height
    glfwSetWindowSize(this.id, this.width, this.height)
    glViewport(0, 0, this.width, this.height)
  }

  def setTitle(title: String): Unit = {
    this.title = title
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
  def make(context: GLFWContext, width: Int, height: Int, title: String): Resource[IO, GLFWWindow] = Resource.make[IO,GLFWWindow]{
    for{
      window <- IO{
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
    }yield window
  }{ window =>
    for{
      _ <- IO{
        glfwDestroyWindow(window.id)
      }.evalOn(context.ec)
    }yield()
  }
}
