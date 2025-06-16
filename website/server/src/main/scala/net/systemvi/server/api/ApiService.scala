package net.systemvi.server.api

import cats.*
import cats.data.Kleisli
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import net.systemvi.server.services.{GameService, KeyboardService}

val apiService=HttpRoutes.of[IO]{
  case GET -> Root / "keyboards" => Ok(KeyboardService.all().asJson.noSpaces)
  case GET -> Root / "keyboard" / IntVar(id) => Ok(KeyboardService.get(id).asJson.noSpaces)
  case GET -> Root / "games" => Ok(GameService.all().asJson.noSpaces)
  case GET -> Root / "game" / IntVar(id) => Ok(GameService.get(id).asJson.noSpaces)
}