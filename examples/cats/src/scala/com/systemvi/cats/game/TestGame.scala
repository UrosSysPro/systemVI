package com.systemvi.cats.game

import cats.effect.IO

class TestGame extends IOGame{
  override def setup: IO[Unit] = ???

  override def loop: IO[Unit] = ???
}
