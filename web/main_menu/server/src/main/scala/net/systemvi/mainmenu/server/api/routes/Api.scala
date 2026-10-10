package net.systemvi.mainmenu.server.api.routes

import cats.effect.*
import net.systemvi.mainmenu.server.api.controllers.itemController
import net.systemvi.mainmenu.server.api.controllers.categoryController
import net.systemvi.mainmenu.server.api.controllers.restaurantController
import net.systemvi.mainmenu.server.api.controllers.restaurantTableController
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.server.Router

def api(using context: AppContext[IO]): HttpRoutes[IO] =
  Router(
    "items" -> itemController,
    "category" -> categoryController,
    "restaurants" -> restaurantController,
    "restaurant-tables" -> restaurantTableController,
  )
