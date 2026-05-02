package net.systemvi.server.api.routes

import cats.effect.IO
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.api.controllers.*
import org.http4s.*
import org.http4s.server.Router

def website(using context:AppContext[IO]) = Router(
    "/" -> websiteController
)