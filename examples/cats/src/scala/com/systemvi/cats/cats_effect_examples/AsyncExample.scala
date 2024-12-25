package com.systemvi.cats.cats_effect_examples

import cats.effect.{IO, IOApp, Resource}
import cats.*
import cats.effect.kernel.Outcome.{Canceled, Errored, Succeeded}
import cats.implicits.*

import java.util.concurrent.{ExecutorService, Executors, TimeUnit}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

object AsyncExample extends IOApp.Simple {

  extension [A](io: IO[A]) {
    def printThread: IO[A] = io.map { io =>
      println(s"${Thread.currentThread().getName}")
      io
    }
  }

  def customExecutionContext(n: Int): Resource[IO, ExecutionContext] = Resource.make {
    IO {
      var id = 0
      ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(n, runnable => {
        val thread = Thread(runnable, s"custom-pool-$id")
        id += 1
        thread.setDaemon(true)
        thread
      }))
    }
  } { ec =>
    IO {
      ec.shutdown()
      ec.awaitTermination(1, TimeUnit.SECONDS)
    }
  }

  override def run: IO[Unit] = customExecutionContext(4).use { ec =>
    for {

      a1 <- IO.async[String] { cb =>
        IO {
          cb("hello".asRight)
          Some(IO.println("return some of first async").printThread)
        }
      }.evalOn(ec)

      _ <- IO.println(a1)
      _<-IO.cede


      a2 <- IO.async_[String] { cb =>
        cb("hello".asRight)
      }.evalOn(ec)

      _ <- IO.println(a2)

      _ <- IO.unit.printThread

      fiber <- IO.async[String] { cb =>
        for {
          _ <- IO.sleep(10.seconds)
          _ <- IO(cb("hello".asRight))
        } yield Some(IO(println("return some of second async")))
      }.evalOn(ec).start

      _ <- fiber.cancel

      outcome <- fiber.join

      _ <- outcome match
        case Succeeded(value) => IO.println(value)
        case Canceled() => IO.println("outcome is canceled")
        case Errored(_) => IO.println("error")

    } yield ()
  }
}
