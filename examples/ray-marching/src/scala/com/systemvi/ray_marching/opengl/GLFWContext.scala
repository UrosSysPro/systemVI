package com.systemvi.ray_marching.opengl

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL33.*
import scala.concurrent.ExecutionContext

object GLFWContext {
  def make(versionMajor: Int, versionMinor: Int): Resource[IO, Unit] = Resource.make[IO,Unit]{
    IO{
      glfwInit
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, versionMajor)
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, versionMinor)
      glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
      glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE)
    }
  }{ _ =>
    IO{
      glfwTerminate()
    }
  }
}
