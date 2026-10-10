package net.systemvi.website.server.api.routes

import cats.*
import cats.effect.*
import net.systemvi.website.server.api.*
import net.systemvi.website.server.api.middleware.*
import net.systemvi.website.server.api.middleware.cors
import net.systemvi.website.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router

def router(using context:AppContext[IO]) = Router(
  "/api" -> cors(api),
  "/" -> website,
).orNotFound