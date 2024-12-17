package com.systemvi.cats.game

import cats.effect.IO

class TestGame extends IOGame{
  override def setup: IO[Unit] = IO.println("setup")

  override def loop: IO[Unit] = IO.println("loop")

  override def dispose: IO[Unit] = IO.println("dispose")
}
