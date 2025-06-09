package net.systemvi.server.api

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger

val apiService=HttpRoutes.of[IO]{
  case GET -> Root / "keyboards" => Ok("keyboards")
  case GET -> Root / "keyboard" / id => Ok(s"keyboard $id")
  case GET -> Root / "games" => Ok("games")
  case GET -> Root / "game" / id => Ok(s"game $id")
}