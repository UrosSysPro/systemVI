package net.systemvi.server.api.routes

import cats.effect.IO
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.api.controllers.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.Router
import net.systemvi.server.api.middleware.JwtAuthMiddleware

def api(using context:AppContext[IO]) = Router(
  "manufacturers" -> manufacturerController,
  "switches" -> switchController,
  "keyboards" -> keyboardController,
  "applications" -> applicationController,
  "user" -> JwtAuthMiddleware[IO].apply(using context)(userController),
  "auth" -> Router(
    "google" -> googleAuthController
  ),
)