package com.systemvi.cats.cats_effect_examples

import cats.effect.{IO, IOApp}
import cats.*
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.implicits.*

import scala.concurrent.duration.*

object AsyncExample extends IOApp.Simple {

  extension [A](io: IO[A]) {
    def printThread: IO[A] = io.map { io =>
      println(s"thread ${Thread.currentThread().getName}")
      io
    }
  }

  override def run: IO[Unit] = for {
    a1 <- IO.async[String] { cb =>
      IO {
        cb("hello".asRight)
        Some(IO.unit.printThread)
      }
    }.printThread
    _ <- IO.println(a1).printThread

    a2 <- IO.async_[String] { cb =>
      cb("hello".asRight)
    }.printThread
    _ <- IO.println(a2).printThread

    fiber <- IO.async[String] { cb =>
      for {
        _ <- IO.sleep(10.seconds)
        _ <- IO(cb("hello".asRight))
      } yield Some(IO(println("async canceled")))
    }.start
    _ <- fiber.cancel

    outcome <- fiber.join
    _ <- IO {
      outcome match
        case Succeeded(value) => println(value)
        case Canceled() => println("outcome is canceled")
        case Errored(_) => println("error")
    }
  } yield ()
}
