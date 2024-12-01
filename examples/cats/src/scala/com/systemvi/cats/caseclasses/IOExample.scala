package com.systemvi.cats.caseclasses

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration.*
import cats.*
import cats.implicits.{catsSyntaxTuple2Parallel, *}

object IOExample extends IOApp {

  extension [F](io: IO[F]) {
    def printDegugInfo: IO[F] = io.map { value =>
      println(s"this is debug info about $io")
      value
    }
    def printThreadName: IO[F] = io.map { value =>
      println(Thread.currentThread().getName)
      value
    }
  }

  def countDown(n: Int): IO[List[Unit]] = {
    (0 until n).map { value =>
      IO.sleep((value * 500).milliseconds) *> IO.println(value).printThreadName
    }.toList.parSequence
  }

  def readAndPrint(): IO[Unit] = {
    for {
      _ <- IO(0) *> IO.println("write something")
      value <- IO.readLine
      _ <- IO.println(s"value: $value")
    } yield ()
  }

  override def run(args: List[String]): IO[ExitCode] = {
    //    readAndPrint().as(ExitCode.Success)
    (countDown(20), countDown(10))
      .parMapN[IO[Int]](
        (_, _) => IO(0)
      ).as(ExitCode.Success)
  }
}
