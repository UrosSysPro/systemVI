package com.systemvi.cats.game2

import cats.*
import cats.implicits.*
import cats.effect.*

import scala.concurrent.ExecutionContext

class TestGameState() extends IOGame.State{
  override def shouldClose: IO[Boolean] = true.pure[IO]

  override def renderExecutionContext: ExecutionContext = null
}

object TestGame extends IOGame[TestGameState]{
  override def setup: IO[TestGameState] = for{
    _<-IO.println("setup")
  }yield TestGameState()

  override def loop(delta: Float, state: TestGameState): IO[Unit] = IO.println("loop")

  override def dispose(state: TestGameState): IO[Unit] = IO.println("dispose")
}
