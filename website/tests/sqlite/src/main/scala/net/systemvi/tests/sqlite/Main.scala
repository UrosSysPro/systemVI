package net.systemvi.tests.sqlite

import cats.effect.*
import cats.*
import cats.effect.implicits.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for{
    _ <- IO.print("hello from sqlite test")
//    ta <-
  }yield()
}
