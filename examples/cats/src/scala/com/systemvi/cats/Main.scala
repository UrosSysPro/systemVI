package com.systemvi.cats

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration.*


object Main extends IOApp:

  override def run(args: List[String]): IO[ExitCode] = program.as(ExitCode.Success)

  val program: IO[Unit] = for
    _ <- IO.println("hello").map{value=>
      println(Thread.currentThread().getName)
      value
    }
  yield IO.unit

