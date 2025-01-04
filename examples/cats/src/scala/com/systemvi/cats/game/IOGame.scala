package com.systemvi.cats.game

import cats.*
import cats.effect.*
import cats.implicits.*

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

abstract class IOGame {

  extension [A](io:IO[A]){
    def printThread:IO[A]=io.map {value=>
      println(s"${Thread.currentThread().getName} $value")
      value
    }
  }

  var ec: Option[ExecutionContext] = None

  def setup: IO[Unit]

  def loop: IO[Unit]

  def dispose: IO[Unit]

  def run: IO[Unit] = {

    def renderExecutionContextResource=Resource.make[IO,Unit]{
      IO{
        val context = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor((runnable) => {
          val thread = Thread(runnable)
          thread.setDaemon(true)
          thread.setName(s"[render-thread-${thread.getName}]")
          thread
        }))
        ec = context.some
      }
    }{_=>
      IO.unit
    }

    renderExecutionContextResource.use{_=>
      val ec=this.ec match
        case Some(ec)=>ec
      for {
        _ <- setup.evalOn(ec)
        _ <- loop.evalOn(ec)
        _ <- dispose.evalOn(ec)
      } yield IO.unit
    }
  }
}
