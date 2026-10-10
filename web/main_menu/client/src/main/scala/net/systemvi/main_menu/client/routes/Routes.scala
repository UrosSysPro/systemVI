package net.systemvi.main_menu.client.routes

import com.raquo.waypoint.*
import Pages.*

object Routes {
  val categoriesRoute: Route.Total[CategoriesPage.type, Unit] = Route.static(CategoriesPage, root / endOfSegments)

  val itemsRoute: Route.Total[ItemsPage.type, Unit] = Route.static(ItemsPage, root / "items" / endOfSegments)

  val notFoundRoute: Route.Total[NotFoundPage.type, Unit] = Route.static(NotFoundPage, root / "404" / endOfSegments)
}
