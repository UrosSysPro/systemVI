package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.category.CategoryCard
import net.systemvi.main_menu.client.components.{listColumn, pageColumn, pageShell, pageTitle}
import net.systemvi.main_menu.client.routes.Pages.*

import java.util.UUID

/** Read-only menu for a restaurant: only its top-level categories. */
def RestaurantMenuPageView(restaurantId: UUID): HtmlElement = {
  val categories = MenuApi.categoriesForRestaurant(restaurantId).toSignal(Nil)

  pageShell(List(
    pageColumn(List(
      pageTitle("Menu"),
      div(child <-- categories.map {
        case Nil => div("No categories yet.")
        case list => listColumn(list.map(category => CategoryCard(category, CategoryMenuPage(restaurantId, category.uuid))))
      }),
    ))
  ))
}
