package net.systemvi.server.api.routes

import cats.*
import cats.effect.*
import net.systemvi.server.api.*
import net.systemvi.server.api.middleware.*
import net.systemvi.server.persistance.contexts.ApplicationContext
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router

def router(using context:ApplicationContext[IO]) = Router(
  "/api" -> cors(api),
  "/" -> website
).orNotFound
