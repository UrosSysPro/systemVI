package com.systemvi.cats.game

import com.systemvi.engine.window.Window
import org.lwjgl.glfw.GLFW.*
import cats.implicits.*
import cats.*
import cats.effect.*
import cats.effect.implicits.*

class TestGame extends IOGame{

  var window:Option[Window]=None

  override def setup: IO[Unit] = IO{
    glfwInit()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE)
    window=Window(800,600,"title").some
  }

  override def loop: IO[Unit] = IO{

  }

  override def dispose: IO[Unit] = IO{
    window.foldMap(_.close())
    glfwTerminate()
  }
}
