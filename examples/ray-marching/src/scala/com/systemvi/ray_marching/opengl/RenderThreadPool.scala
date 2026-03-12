package com.systemvi.ray_marching.opengl

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*

import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
import scala.concurrent.ExecutionContext

object RenderThreadPool {
  def make(name:String): Resource[IO, ExecutionContext] = Resource.make[IO,ExecutionContext]{
    IO{
      ExecutionContext.fromExecutorService {
        Executors.newSingleThreadExecutor { (r: Runnable) =>
          val t = Thread(r, name)
          t.setDaemon(true)
          t
        }
      }
    }
  }{ ec =>
    IO.unit
  }
}
