package com.systemvi.cats.cats_effect_examples

import cats.effect.kernel.Ref

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*

object RefExample extends IOApp.Simple {
  var counter=0
  val n=10000
  override def run: IO[Unit] = for {
    ref <- Ref.of[IO, Int](0)
    _ <- (0 until n).toList.map(_ => ref.update(_ + 1)).parSequence
    _ <- (0 until n).toList.map(_ => IO{counter+=1}).parSequence
    value<-ref.get
    _<-IO.println(s"ref value: $value")
    _<-IO.println(s"counter $counter")
  } yield ()
}
