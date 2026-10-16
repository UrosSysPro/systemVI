package net.systemvi.main_menu.client

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.views.{CategoryMenuPageView, ItemPageView, LoginPageView, RestaurantMenuPageView, RestaurantsPageView, SubcategoryMenuPageView, TableItemPageView, TableMenuPageView, WelcomePageView}
import net.systemvi.main_menu.client.views.admin.AdminDashboardPageView
import net.systemvi.main_menu.client.views.admin.category.{AdminCategoriesPageView, AdminCategoryPageView, AdminCreateCategoryPageView, AdminEditCategoryPageView}
import net.systemvi.main_menu.client.views.admin.item.{AdminCreateItemPageView, AdminEditItemPageView}
import net.systemvi.main_menu.client.views.admin.table.{AdminCreateTablePageView, AdminEditTablePageView, AdminTablesPageView}
import org.scalajs.dom

val splitter = SplitRender[Page, HtmlElement](Router.currentPageSignal)
  .collectStatic(LoginPage)        { LoginPageView() }
  .collectStatic(WelcomePage)      { WelcomePageView() }
  .collectStatic(RestaurantsPage)  { RestaurantsPageView() }
  .collect[RestaurantMenuPage]     { page => RestaurantMenuPageView(page.restaurantId) }
  .collect[CategoryMenuPage]       { page => CategoryMenuPageView(page.restaurantId, page.categoryId) }
  .collect[SubcategoryMenuPage]    { page => SubcategoryMenuPageView(page.restaurantId, page.categoryId, page.subcategoryId) }
  .collect[ItemPage]               { page => ItemPageView(page.restaurantId, page.itemId) }
  .collect[TableMenuPage]          { page => TableMenuPageView(page.restaurantId, page.tableId) }
  .collect[TableItemPage]          { page => TableItemPageView(page.restaurantId, page.tableId, page.itemId) }

  .collect[AdminDashboardPage]     { page => AdminDashboardPageView(page.restaurantId) }
  .collect[AdminTablesPage]        { page => AdminTablesPageView(page.restaurantId) }
  .collect[AdminCategoriesPage]    { page => AdminCategoriesPageView(page.restaurantId) }
  .collect[AdminCategoryPage]      { page => AdminCategoryPageView(page.restaurantId, page.categoryId) }
  .collect[AdminCreateCategoryPage]{ page => AdminCreateCategoryPageView(page.restaurantId, page.parentId) }
  .collect[AdminEditCategoryPage]  { page => AdminEditCategoryPageView(page.restaurantId, page.categoryId) }
  .collect[AdminCreateItemPage]    { page => AdminCreateItemPageView(page.restaurantId, page.categoryId) }
  .collect[AdminEditItemPage]      { page => AdminEditItemPageView(page.restaurantId, page.categoryId, page.itemId) }
  .collect[AdminCreateTablePage]   { page => AdminCreateTablePageView(page.restaurantId) }
  .collect[AdminEditTablePage]     { page => AdminEditTablePageView(page.restaurantId, page.tableId) }

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
