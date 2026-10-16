package net.systemvi.mainmenu.server.api.routes

import cats.effect.*
import net.systemvi.mainmenu.server.api.controllers.*
import net.systemvi.mainmenu.server.api.middleware.{copyCookieToHeader, jwtAuth}
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.server.Router

def api(using context: AppContext[IO]): HttpRoutes[IO] = {
  val auth = jwtAuth[IO]
  val copy = copyCookieToHeader[IO]

  Router(
    "items" -> itemController,
    "category" -> categoryController,
    "restaurants" -> restaurantController,
    "restaurant-tables" -> restaurantTableController,
    "image" -> imageController,
    "auth" -> Router(
      "google" -> googleAuthController,
    ),
    "user" -> copy(auth(userController)),
  )
}
