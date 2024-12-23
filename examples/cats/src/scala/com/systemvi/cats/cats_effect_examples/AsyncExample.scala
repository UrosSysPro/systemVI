package com.systemvi.cats.cats_effect_examples

import cats.effect.{IO, IOApp}
import cats.*
import cats.implicits.*

object AsyncExample extends IOApp.Simple {

  extension[A](io:IO[A]){
    def printThread: IO[A] = io.map{io=>
      println(s"thread ${Thread.currentThread().getName}")
      io
    }
  }

  override def run: IO[Unit] = for {
    a1<-IO.async[String]{cb=>IO{
      cb("hello".asRight)
      Some(IO.unit.printThread)
    }}.printThread
    _<-IO.println(a1).printThread
    a2<-IO.async_[String]{cb=>
      cb("hello".asRight)
    }.printThread
    _<-IO.println(a2).printThread
  } yield ()
}
