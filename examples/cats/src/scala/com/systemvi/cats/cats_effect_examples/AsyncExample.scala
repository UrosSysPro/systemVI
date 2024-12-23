package com.systemvi.cats.cats_effect_examples

import cats.effect.{IO, IOApp}
import cats.*
import cats.implicits.*

object AsyncExample extends IOApp.Simple {
  override def run: IO[Unit] = for {
    a1<-IO.async[String]{cb=>IO{

    }}
    _<-IO.println(a1)
    a2<-IO.async_[String]{cb=>
      cb("hello".asRight)
    }
    _<-IO.println(a2)
  } yield ()
}
