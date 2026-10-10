package net.systemvi.website.server.api.routes

import cats.effect.IO
import net.systemvi.website.server.api.controllers.*
import net.systemvi.website.server.api.controllers.websiteController
import net.systemvi.website.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.server.Router

def website(using context:AppContext[IO]) = Router(
    "/" -> websiteController
)