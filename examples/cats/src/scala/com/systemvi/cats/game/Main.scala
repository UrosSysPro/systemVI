package com.systemvi.cats.game

import cats.effect.*
import cats.*
import cats.effect.implicits.*
import cats.implicits.*
import scala.concurrent.duration.*

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    game <- IO(TestGame())
    _ <- game.run
  } yield ()

//  def getTime: IO[Long] = IO {
//    System.nanoTime()
//  }
//
//  def renderLoop(game: IOGame): IO[Unit] = for {
//    frameStart <- getTime
//    _ <- game.loop
//    frameEnd <- getTime
//    frameTime = (frameEnd - frameStart).nanos
//    _ <- IO.println(
//      s"seconds: ${frameTime.toSeconds % 1000} milliseconds: ${frameTime.toMillis % 1000}".stripMargin
//    )
//    _ <- IO.sleep(1.seconds)
//  } yield ()
}
