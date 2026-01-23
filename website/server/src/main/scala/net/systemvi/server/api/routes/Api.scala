package net.systemvi.server.api.routes

import cats.effect.IO
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.api.controllers.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

def api(using context:AppContext[IO]) = HttpRoutes.of[IO]{
  case request @ _ ->  "manufacturers" /: tail =>
    manufacturerController(request.withPathInfo(tail)).getOrElse(Response.notFound)
  case request @ _ ->  "switches" /: tail =>
    switchController(request.withPathInfo(tail)).getOrElse(Response.notFound)
  case request @ _ ->  "keyboards" /: tail =>
    keyboardController(request.withPathInfo(tail)).getOrElse(Response.notFound)
  case request @ _ ->  "applications" /: tail =>
    applicationController(request.withPathInfo(tail)).getOrElse(Response.notFound)
}
