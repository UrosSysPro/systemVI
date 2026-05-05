package net.systemvi.server.api.routes

import cats.effect.IO
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.api.controllers.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.server.Router

def api(using context:AppContext[IO]) = Router(
  "manufacturers" -> manufacturerController,
  "switches" -> switchController,
  "keyboards" -> keyboardController,
  "applications" -> applicationController,
  "auth" -> Router(
    "google" -> GoogleAuthController(context).routes
  )
)