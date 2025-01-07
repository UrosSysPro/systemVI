package com.systemvi.cats.game

import cats.*
import cats.effect.*
import cats.implicits.*

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

abstract class IOGame {

  extension [A](io: IO[A]) {
    def printThread: IO[A] = io.map { value =>
      println(s"${Thread.currentThread().getName} $value")
      value
    }
  }

  var targetFps: Int = 165
  var targetFrameTime: FiniteDuration =(1d / targetFps).seconds
  var ec: Option[ExecutionContext] = None

  def setup: IO[Unit]

  def loop: IO[Unit]

  def dispose: IO[Unit]

  def shouldClose: IO[Boolean]

  def run: IO[Unit] = {

    def renderExecutionContextResource = Resource.make[IO, Unit] {
      IO {
        val context = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor((runnable) => {
          val thread = Thread(runnable)
          thread.setDaemon(true)
          thread.setName(s"[render-thread-${thread.getName}]")
          thread
        }))
        ec = context.some
      }
    } { _ =>
      IO.unit
    }

    def gameLoop: IO[Unit] = for {
      startTime <- IO.realTime
      _ <- loop
      endTime <- IO.realTime
      
      diff <- IO.pure(endTime - startTime)
      toSleep <- IO.pure(targetFrameTime - diff)
      
      _ <- if toSleep.toNanos > 0 then IO.sleep(toSleep) else IO.unit
      
      _ <- shouldClose.ifM(IO.unit, gameLoop)
    } yield ()

    renderExecutionContextResource.use { _ =>
      val ec = this.ec match
        case Some(ec) => ec
      for {
        _ <- setup.evalOn(ec)
        _ <- gameLoop.evalOn(ec)
        _ <- dispose.evalOn(ec)
      } yield IO.unit
    }
  }
}
