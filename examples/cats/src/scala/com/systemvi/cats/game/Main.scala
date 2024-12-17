package com.systemvi.cats.game

import cats.effect.*
import cats.*
import cats.effect.implicits.*
import cats.implicits.*
import scala.concurrent.duration.*
object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    game <- IO(TestGame())
    _ <- game.setup
    _ <- (for {
      frameStart<-getTime
      _ <- game.loop
      frameEnd<-getTime
      _<-IO.sleep((frameEnd-frameStart).nanos)
    } yield ()).foreverM
    _ <- game.dispose
  } yield ()

  def getTime: IO[Long] = IO {
    System.nanoTime()
  }
}
