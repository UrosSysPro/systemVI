package net.systemvi.mainmenu.server.api.routes

import cats.effect.*
import net.systemvi.mainmenu.server.api.controllers.menuController
import net.systemvi.mainmenu.server.api.controllers.itemController
import net.systemvi.mainmenu.server.api.controllers.categoryController
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.server.Router

def api(using context: AppContext[IO]): HttpRoutes[IO] =
  Router(
    "menus" -> menuController,
    "items" -> itemController,
    "category" -> categoryController,
  )
