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

trait GLFWWindow(
                      val id: Long,
                      var width:Int,
                      var height: Int,
                      var title: String,
                      val renderer: String,
                      val vendor: String,
                      val version: String,
                      val glslVersion: String,
                      val capabilities: GLCapabilities,
                     ) {
  def makeCurrent(using context: GLFWContext): IO[Unit]
  def setSize(width: Int, height: Int)(using context: GLFWContext): IO[Unit]
  def setTitle(title: String)(using context: GLFWContext): IO[Unit]
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
        val renderer = glGetString(GL_RENDERER)
        val vendor = glGetString(GL_VENDOR)
        val version = glGetString(GL_VERSION)
        val glslVersion = glGetString(GL_SHADING_LANGUAGE_VERSION)
        new GLFWWindow(
          id,
          width,
          height,
          title,
          renderer,
          vendor,
          version,
          glslVersion,
          capabilities
        ) {

          override def makeCurrent(using context: GLFWContext): IO[Unit] = IO{
            glfwMakeContextCurrent(this.id)
            glViewport(0,0,this.width,this.height)
          }.evalOn(context.ec)

          override def setSize(width: Int, height: Int)(using context: GLFWContext): IO[Unit] = IO{
            this.width = width
            this.height = height
            glfwSetWindowSize(this.id,this.width,this.height)
            glViewport(0,0,this.width,this.height)
          }.evalOn(context.ec)

          override def setTitle(title: String)(using context: GLFWContext): IO[Unit] = IO{
            this.title = title
            glfwSetWindowTitle(this.id, this.title)
          }.evalOn(context.ec)
        }
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
