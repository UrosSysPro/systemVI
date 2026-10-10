package net.systemvi.main_menu.client

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.views.{CategoryMenuPageView, RestaurantMenuPageView, RestaurantsPageView, SubcategoryMenuPageView}
import net.systemvi.main_menu.client.views.admin.{AdminCategoriesPageView, AdminCategoryPageView, AdminDashboardPageView, AdminTablesPageView}
import org.scalajs.dom

val splitter = SplitRender[Page, HtmlElement](Router.currentPageSignal)
  .collectStatic(RestaurantsPage)  { RestaurantsPageView() }
  .collect[RestaurantMenuPage]     { page => RestaurantMenuPageView(page.restaurantId) }
  .collect[CategoryMenuPage]       { page => CategoryMenuPageView(page.restaurantId, page.categoryId) }
  .collect[SubcategoryMenuPage]    { page => SubcategoryMenuPageView(page.restaurantId, page.categoryId, page.subcategoryId) }

  .collect[AdminDashboardPage]     { page => AdminDashboardPageView(page.restaurantId) }
  .collect[AdminTablesPage]        { page => AdminTablesPageView(page.restaurantId) }
  .collect[AdminCategoriesPage]    { page => AdminCategoriesPageView(page.restaurantId) }
  .collect[AdminCategoryPage]      { page => AdminCategoryPageView(page.restaurantId, page.categoryId) }

  .collectStatic(NotFoundPage)     { div("page not found") }

val app: Div = div(
  child <-- splitter.signal
)

@main
def main(): Unit = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    app
  )
}
