package net.systemvi.server.api.manufacturer

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

val manufacturerRoutes=HttpRoutes.of[IO]{
  case GET -> Root => Ok("get manufacturer")
  case POST -> Root => Ok("create manufacturer")
  case PUT -> Root => Ok("update manufacturer")
  case DELETE -> Root => Ok("delete manufacturer")
}
