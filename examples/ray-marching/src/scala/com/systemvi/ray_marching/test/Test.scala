package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.opengl.*

import scala.concurrent.duration.DurationInt

object Test extends IOApp.Simple {
  override def run: IO[Unit] = {
    GLFWContext.make(3,3).use{ context =>
      GLFWWindow.make(context,800,600,"window").use{ window =>
        for {
          _ <- IO.println(window.renderer)
          _ <- IO.println(window.vendor)
          _ <- IO.println(window.version)
          _ <- IO.println(window.glslVersion)
        } yield ()
      }
    }
  }
}

