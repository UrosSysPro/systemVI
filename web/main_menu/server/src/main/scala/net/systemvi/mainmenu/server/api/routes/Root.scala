package net.systemvi.mainmenu.server.api.routes

import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router

def router(using context: AppContext[IO]): HttpRoutes[IO] =
  Router(
    "/api" -> api,
  )
