package com.systemvi.ray_marching.test

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.systemvi.ray_marching.opengl.*

import scala.concurrent.duration.{Duration, DurationDouble, DurationInt}

object Test extends IOApp.Simple {
  private def resources: Resource[IO, (GLFWContext, GLFWWindow)] = for{
    context <- GLFWContext.make(3,3)
    window <- GLFWWindow.make(context,800,600,"window")
  } yield (context,window)

  private val targetFPS:Int =  20
  private val targetFrameTime: Double = 1000d / targetFPS

  private def loop(running: Ref[IO, Boolean], lastFrameStartRef: Ref[IO,Double], context:GLFWContext, window: GLFWWindow): IO[Unit] = {
      running.get.flatMap {
        case false => IO.unit // Exit loop
        case true =>
          for
            lastFrameStart <- lastFrameStartRef.get
            startTime <- context.getTime
            _ <- update(startTime - lastFrameStart, running, context, window)
            endTime <- context.getTime
            _ <- lastFrameStartRef.set(startTime)
            elapsed = endTime - startTime
            sleepTime = (targetFrameTime - elapsed).millis
            _ <- IO.sleep(sleepTime).whenA(sleepTime > Duration.Zero)
            _ <- loop(running, lastFrameStartRef, context, window)
          yield ()
    }

  }

  def update(delta: Double,running: Ref[IO,Boolean], context: GLFWContext, window: GLFWWindow): IO[Unit] = {
    given GLFWContext = context
    for{
      //input
      _ <- window.pollEvents
      //update
      shouldClose <- window.shouldClose
      _ <- running.set(!shouldClose)
      //draw
      _ <- window.swapBuffers
    }yield ()
  }


  override def run: IO[Unit] = {
    resources.use{ (context, window) =>
      for {
        _ <- IO.println(window.renderer)
        _ <- IO.println(window.vendor)
        _ <- IO.println(window.version)
        _ <- IO.println(window.glslVersion)
        running <- Ref.of[IO,Boolean](true)
        lastFrameStart <- Ref.of[IO,Double](-targetFrameTime)
        _ <- loop(running, lastFrameStart, context, window)
      } yield ()
    }
  }
}

