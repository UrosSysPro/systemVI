package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.{linkRow, listColumn, pageColumn, pageShell, pageTitle}
import net.systemvi.main_menu.client.routes.Pages.*

/** Landing page: pick one of the available restaurants. */
def RestaurantsPageView(): HtmlElement = {
  val restaurants = MenuApi.restaurants.toSignal(Nil)

  pageShell(List(
    pageColumn(List(
      pageTitle("Restaurants"),
      div(child <-- restaurants.map {
        case Nil => div("No restaurants yet.")
        case list => listColumn(list.map(restaurant => linkRow(restaurant.name, RestaurantMenuPage(restaurant.uuid))))
      }),
    ))
  ))
}
