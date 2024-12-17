package com.systemvi.cats.game

import cats.effect.*
import cats.*
import cats.effect.implicits.*
import cats.implicits.*

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    game <- IO(TestGame())
  } yield ()
}
