package com.systemvi.cats.game2

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  override def run: IO[Unit] = TestGame.run
}
