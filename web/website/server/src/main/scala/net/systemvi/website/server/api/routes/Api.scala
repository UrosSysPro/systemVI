package net.systemvi.website.server.api.routes

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import net.systemvi.website.server.api.controllers.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.Router
import net.systemvi.website.server.api.middleware.*
import net.systemvi.website.server.api.controllers.{applicationController, googleAuthController, keyboardController, manufacturerController, switchController, userController}
import net.systemvi.website.server.api.middleware.{copyCookieToHeader, jwtAuth}
import net.systemvi.website.server.persistance.contexts.AppContext
import org.http4s.Method.OPTIONS

def api(using context:AppContext[IO]) = {

  val auth = jwtAuth[IO]
  val copy = copyCookieToHeader[IO]

  Router(
    "manufacturers" -> manufacturerController,
    "switches" -> switchController,
    "keyboards" -> keyboardController,
    "applications" -> applicationController,
    "user" -> copy(auth(userController)),
    "auth" -> Router(
      "google" -> googleAuthController
    ),
  )
}