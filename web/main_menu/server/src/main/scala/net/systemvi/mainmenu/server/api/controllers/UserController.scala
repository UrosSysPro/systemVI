package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.syntax.*
import net.systemvi.mainmenu.common.dto.UserDto
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*

/** Returns the user carried by the session token. */
def userController: AuthedRoutes[UserDto, IO] =
  AuthedRoutes.of[UserDto, IO] {
    case GET -> Root as user =>
      Ok(user.asJson)
  }
