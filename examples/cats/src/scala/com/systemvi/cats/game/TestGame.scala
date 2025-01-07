package com.systemvi.cats.game

import cats.*
import cats.effect.*
import cats.implicits.*
import com.systemvi.engine.camera.Camera3
import com.systemvi.engine.renderers.ShapeRenderer2
import com.systemvi.engine.ui.utils.data.Colors
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.utils.Utils.Buffer
import com.systemvi.engine.window.Window
import org.lwjgl.glfw.GLFW.*

class TestGame extends IOGame {

  var window: Option[Window] = None
  var camera: Option[Camera3] = None
  var polygonRenderer: Option[ShapeRenderer2] = None

  given Conversion[Int, Float] = value => value.toFloat

  override def setup: IO[Unit] = for {
    _ <- IO {
      glfwInit()
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
      glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
      glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE)
    }.printThread
    _ <- IO {
      window = Window(800, 600, "title").some
      for (window <- window)
        camera = Camera3.builder2d()
          .size(window.getWidth, window.getHeight)
          .position(window.getWidth / 2, window.getHeight / 2)
          .build().some

      polygonRenderer = ShapeRenderer2().some
      for(camera<-camera;polygonRenderer<-polygonRenderer){
        polygonRenderer.view(camera.view)
        polygonRenderer.projection(camera.projection)
      }
    }
  } yield ()


  override def loop: IO[Unit] = for {
    _ <- IO(window.foldMap(_.pollEvents()))
    _ <- IO(Utils.clear(Colors.black, Buffer.COLOR_BUFFER))

    _<-IO{
      for(renderer<-polygonRenderer){
//        renderer.draw(Triang)
      }
    }

    _ <- IO(window.foldMap(_.swapBuffers()))
  } yield ()

  override def dispose: IO[Unit] = for {
    _ <- IO(window.foldMap(_.close())).printThread
    _ <- IO(glfwTerminate()).printThread
  } yield ()

  override def shouldClose: IO[Boolean] = IO {
    window match
      case Some(window) => window.shouldClose()
      case _ => true
  }
}
