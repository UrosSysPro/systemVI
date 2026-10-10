package net.systemvi.main_menu.client.views.admin

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.{adminLayout, linkRow, listColumn, pageTitle}
import net.systemvi.main_menu.client.routes.Pages.*

import java.util.UUID

/** Dashboard with an embedded, centered preview of the restaurant's menu. */
def AdminDashboardPageView(restaurantId: UUID): HtmlElement = {
  val categories = MenuApi.categoriesForRestaurant(restaurantId).toSignal(Nil)

  adminLayout(
    restaurantId,
    AdminDashboardPage(restaurantId),
    div(
      display.flex,
      flexDirection.column,
      alignItems.center,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(900),
      pageTitle("Dashboard"),
      div(
        display.flex,
        flexDirection.column,
        gap.rem(0.5),
        width.percent(100),
        maxWidth.px(600),
        h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), "Menu"),
        div(child <-- categories.map {
          case Nil => div("No categories yet.")
          case list => listColumn(list.map(category => linkRow(category.name, CategoryMenuPage(restaurantId, category.uuid))))
        }),
      ),
    )
  )
}
