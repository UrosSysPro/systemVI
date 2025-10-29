package net.systemvi.server.api

import cats.*
import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import io.circe.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import net.systemvi.server.services.{EngineService, GameService, KeyboardService}
import net.systemvi.common.model.*

val apiService=HttpRoutes.of[IO]{
  case GET -> Root / "hello" => Ok("hello world")
  case GET -> Root / "keyboards" =>for{
    response<-Ok(KeyboardService.all().asJson)
  } yield response
  case GET -> Root / "keyboard" / IntVar(id) => Ok(KeyboardService.get(id).asJson)
  case GET -> Root / "games" => Ok(GameService.all().asJson)
  case GET -> Root / "game" / IntVar(id) => Ok(GameService.get(id).asJson)
  case GET -> Root / "engine" / "examples" => Ok(EngineService.get().asJson)
}