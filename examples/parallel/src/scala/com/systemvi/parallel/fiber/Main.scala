package com.systemvi.parallel.fiber

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import scala.concurrent.duration.*

object Main extends IOApp.Simple {
  extension[A](io:IO[A]){
    def debugPrint(message:String): IO[A] = {
      io.map{value=>
        println(s"${Thread.currentThread().getName}: $message")
        value
      }
    }
  }

  override def run: IO[Unit] = for{
    start<-IO{System.nanoTime()}
    ref<-Ref.of[IO,Int](0)
    _ <- List.range(0,10000).map(e=>ref.update(value=>value+1)).parSequence
    end<-IO{System.nanoTime()}
    diff=end-start
    value<-ref.get
    time=diff.nanos
    _<-IO.println(s"$value in time ${time.toMillis}ms ${time.toMicros%1000}micro ${diff%1000}ns")
  }yield()
}