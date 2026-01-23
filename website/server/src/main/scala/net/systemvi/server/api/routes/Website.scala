package net.systemvi.server.api.routes

import cats.effect.IO
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.api.controllers.*
import org.http4s.*

def website(using context:AppContext[IO]) = HttpRoutes.of[IO]{
  case request =>
    websiteController(request).getOrElse(Response.notFound)
}
