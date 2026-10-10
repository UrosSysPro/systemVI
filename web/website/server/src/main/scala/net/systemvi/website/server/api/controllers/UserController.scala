package net.systemvi.website.server.api.controllers

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.implicits.*
import org.http4s.dsl.io.*

import java.util.UUID
import net.systemvi.website.server.persistance.models.*
import net.systemvi.website.server.persistance.contexts.AppContext
import net.systemvi.website.server.persistance.models.User

def userController(using context: AppContext[IO]) = AuthedRoutes.of[User, IO]{
  case GET -> Root as user => for{
    response <- Ok(user.asJson)
  } yield response
}
