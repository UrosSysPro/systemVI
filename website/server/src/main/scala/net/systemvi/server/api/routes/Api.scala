package net.systemvi.server.api.routes

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.api.controllers.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.Router
import net.systemvi.server.api.middleware.*
import org.http4s.Method.OPTIONS

def api(using context:AppContext[IO]) = {

  val auth = jwtAuth[IO]
  val copy = copyCookieToHeader[IO]
  val userRoutes: HttpRoutes[IO] = auth(userController)
  Router(
    "manufacturers" -> manufacturerController,
    "switches" -> switchController,
    "keyboards" -> keyboardController,
    "applications" -> applicationController,
    "user" -> copy(userRoutes),
    "auth" -> Router(
      "google" -> googleAuthController
    ),
  )
}