package com.systemvi.ray_marching.opengl

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.{GL_RENDERER, GL_VENDOR, GL_VERSION, glGetString, glViewport}
import org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION
import org.lwjgl.opengl.GL33.*

import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
import scala.concurrent.ExecutionContext

case class GLFWWindow(
                      id: Long,
                      width:Int,
                      height: Int,
                      title: String,
                      renderer:String,
                      vendor:String,
                      version:String,
                      glslVersion:String,
                     ) {
  
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
        GLFWWindow(
          id,
          width,
          height,
          title,
          renderer,
          vendor,
          version,
          glslVersion
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
