package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.{ItemDto, RestaurantTableDto}
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.item.ItemDetail
import net.systemvi.main_menu.client.components.{linkRow, pageColumn, pageShell}
import net.systemvi.main_menu.client.routes.Pages.*

import java.util.UUID

/**
 * Item page for a customer at a table. Ordering is not built yet, so the
 * add-to-cart slot renders as a disabled placeholder.
 */
def TableItemPageView(restaurantId: UUID, tableId: UUID, itemId: UUID): HtmlElement = {
  val item = MenuApi.item(itemId).toSignal(Option.empty[ItemDto])
  val table = MenuApi.restaurantTable(tableId).toSignal(Option.empty[RestaurantTableDto])

  pageShell(List(
    pageColumn(List(
      linkRow("Back to menu", TableMenuPage(restaurantId, tableId)),
      div(color("#6b7280"), child.text <-- table.map(_.map(value => s"Table: ${value.name}").getOrElse(""))),
      div(child <-- item.map {
        case Some(value) => ItemDetail(value, List(addToCartPlaceholder))
        case None        => div("Item not found.")
      }),
    ))
  ))
}

/** Stand-in for the cart, which is not built yet. */
private def addToCartPlaceholder: HtmlElement =
  button(
    "Add to cart",
    disabled := true,
    paddingTop.rem(0.6),
    paddingBottom.rem(0.6),
    paddingLeft.rem(1.1),
    paddingRight.rem(1.1),
    border("1px solid #e2e4e8"),
    borderRadius.rem(0.5),
    backgroundColor("#f2f3f5"),
    color("#9aa0a6"),
    cursor.notAllowed,
    fontSize.rem(0.95),
    fontWeight.bold,
  )
