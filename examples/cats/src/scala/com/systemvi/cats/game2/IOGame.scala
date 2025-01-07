package com.systemvi.cats.game2

import cats.*
import cats.effect.*
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

abstract class IOGame[S<:IOGame.State] {

  extension [A](io: IO[A]) {
    def printThread: IO[A] = io.map { value =>
      println(s"${Thread.currentThread().getName} $value")
      value
    }
  }

  var targetFps: Int = 165
  var targetFrameTime: FiniteDuration =(1d / targetFps).seconds

  def setup: IO[S]

  def loop(delta:Float, state:S): IO[Unit]

  def dispose(state:S): IO[Unit]

  def run: IO[Unit] = {

    def gameLoop(state:S): IO[Unit] = for {
      startTime <- IO.realTime
      _ <- loop(0,state)
      endTime <- IO.realTime

      diff <- IO.pure(endTime - startTime)
      toSleep <- IO.pure(targetFrameTime - diff)

      _ <- if toSleep.toNanos > 0 then IO.sleep(toSleep) else IO.unit

      _ <- state.shouldClose.ifM(IO.unit, gameLoop(state))
    } yield ()

    def stateResource:Resource[IO,S]=Resource.make[IO,S](setup)(dispose)

    stateResource.use { state =>
      for {
        _ <- gameLoop(state)//.evalOn(state.renderExecutionContext)
      } yield ()
    }
  }
}

object IOGame {
  abstract class Basic extends IOGame{

  }
  object Basic {
    abstract class State extends IOGame.State{

    }
  }
  abstract class State  {
    def shouldClose:IO[Boolean]
    def renderExecutionContext:ExecutionContext
  }
}
