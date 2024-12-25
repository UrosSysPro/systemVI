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
      println(s"thread ${Thread.currentThread().getName}")
      io
    }
  }

  def fixedThreadPool: Resource[IO, ExecutorService] = Resource.make {
    IO {
      Executors.newFixedThreadPool(4,runnable=> {
        val thread = Thread(runnable, s"async-context")
        thread.setDaemon(true)
        thread
      })
    }
  } { pool =>
    IO {
      pool.awaitTermination(1, TimeUnit.SECONDS)
    }
  }

  def asyncExecutionContext:Resource[IO,ExecutionContext]=fixedThreadPool.map[ExecutionContext](es=>ExecutionContext.fromExecutorService(es))

  override def run: IO[Unit] = asyncExecutionContext.use { ec =>
    for {
      a1 <- IO.async[String] { cb =>
        IO {
          cb("hello".asRight)
          Some(IO.println("return some of first async").printThread)
        }
      }.printThread.evalOn(ec)

      _ <- IO.println(a1).printThread

      a2 <- IO.async_[String] { cb =>
        cb("hello".asRight)
      }.printThread.evalOn(ec)

      _ <- IO.println(a2).printThread

      fiber <- IO.async[String] { cb =>
        for {
          _ <- IO.sleep(10.seconds)
          _ <- IO(cb("hello".asRight))
        } yield Some(IO(println("return some of second async")).printThread)
      }.evalOn(ec).start

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
}
