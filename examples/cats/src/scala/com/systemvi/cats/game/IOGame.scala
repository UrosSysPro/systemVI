package com.systemvi.cats.game

import cats.effect.*
import cats.effect.implicits.*
import cats.*
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

  var executionContext: ExecutionContext = null

  def setup: IO[Unit]

  def loop: IO[Unit]

  def dispose: IO[Unit]

  def run: IO[Unit] = {
    def renderSetup: IO[Unit] = for {
      _ <- IO {
        val context = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor((runnable) => {
          val thread = Thread(runnable)
          thread.setDaemon(true)
          thread.setName(s"[render-thread-${thread.getName}]")
          thread
        }))
        executionContext = context
      }
      _ <- setup.evalOn(executionContext)
    } yield ()

    def renderLoop: IO[Unit] = for {
      _ <- loop.evalOn(executionContext)
    } yield ()

    def renderDispose: IO[Unit] = for {
      _ <- dispose.printThread.evalOn(executionContext)
      _<-IO.println("eee").printThread
    } yield ()

    for {
      _ <- renderSetup
      _ <- renderLoop
      _ <- renderDispose
    } yield ()
  }
}
