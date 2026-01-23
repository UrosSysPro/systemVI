package net.systemvi.server.api.routes

import cats.effect.IO
import net.systemvi.server.persistance.contexts.ApplicationContext
import net.systemvi.server.api.controllers.*
import org.http4s.*

def website(using context:ApplicationContext[IO]) = HttpRoutes.of[IO]{
  case request =>
    websiteController(request).getOrElse(Response.notFound)
}
