package net.systemvi.server.api.routes

import cats.*
import cats.effect.*
import net.systemvi.server.api.*
import net.systemvi.server.api.middleware.*
import net.systemvi.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router

def Router(using context:AppContext[IO]) = Router(
  "/api" -> cors(api),
  "/" -> website
).orNotFound
