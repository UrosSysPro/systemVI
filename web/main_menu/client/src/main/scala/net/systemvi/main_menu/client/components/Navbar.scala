package net.systemvi.main_menu.client.components

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router

def Navbar(): HtmlElement =
  div(
    display.flex,
    flexDirection.row,
    alignItems.center,
    justifyContent.spaceBetween,
    padding.rem(1),
    backgroundColor("#22272d"),
    color("#ffffff"),
    span(fontSize.rem(1.25), fontWeight.bold, "Main Menu"),
    div(
      display.flex,
      flexDirection.row,
      gap.rem(1),
      NavLink("Categories", CategoriesPage),
      NavLink("Items", ItemsPage),
    ),
  )

private def NavLink(name: String, page: Page): HtmlElement =
  a(
    Router.navigateTo(page),
    cursor.pointer,
    color("#ffffff"),
    textDecoration("none"),
    name,
  )
