package com.systemvi.cats.http4s_examples.database

import cats.effect.*
import com.comcast.ip4s.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.ember.server.*
import org.http4s.implicits.*
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Main extends IOApp.Simple{
  given Slf4jFactory[IO] = Slf4jFactory.create[IO]

  private def routes = HttpRoutes.of[IO] {
    case GET -> Root / "write" => for {
      _ <- IO.println("test route called")
    } yield Response(Status.Ok)
    
    case GET -> Root / "read" => for {
      _ <- IO.println("test route called")
    } yield Response(Status.Ok)
  }

  private def router = Router("/" -> routes).orNotFound

  private def server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port"8080")
    .withHttpApp(router)
    .build

  override def run: IO[Unit] =
    server.use { server =>
      for {
        _ <- IO.never
      } yield ()
    }
}
