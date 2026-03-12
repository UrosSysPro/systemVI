package com.systemvi.ray_marching.opengl

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.{GL_RENDERER, GL_VENDOR, GL_VERSION, glGetString}
import org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION
import org.lwjgl.opengl.GL33.*

import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
import scala.concurrent.ExecutionContext

trait GLFWContext(
                   val versionMajor: Int,
                   val versionMinor: Int,
                   val ec: ExecutionContext
                 ){
  def getTime:IO[Double]
}

object GLFWContext {
  def make(versionMajor: Int, versionMinor: Int,ec: ExecutionContext): Resource[IO, GLFWContext] = Resource.make[IO,GLFWContext]{
    for{
      context <- IO{
        glfwInit
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, versionMajor)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, versionMinor)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE)
        new GLFWContext(
          versionMajor,
          versionMinor,
          ec
        ) {
          override def getTime: IO[Double] = IO{ glfwGetTime() }.evalOn(this.ec)
        }
      }.evalOn(ec)
    } yield context
  }{ context =>
    for{
      _ <- IO{
        glfwTerminate()
      }.evalOn(context.ec)
    }yield()
  }
}
