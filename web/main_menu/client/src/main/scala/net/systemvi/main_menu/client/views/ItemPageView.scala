package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.item.ItemDetail
import net.systemvi.main_menu.client.components.{linkRow, pageColumn, pageShell}
import net.systemvi.main_menu.client.routes.Pages.*

import java.util.UUID

/** Read-only item page, reached from the public menu. */
def ItemPageView(restaurantId: UUID, itemId: UUID): HtmlElement = {
  val item = MenuApi.item(itemId).toSignal(Option.empty[ItemDto])

  pageShell(List(
    pageColumn(List(
      linkRow("Back to menu", RestaurantMenuPage(restaurantId)),
      div(child <-- item.map {
        case Some(value) => ItemDetail(value)
        case None        => div("Item not found.")
      }),
    ))
  ))
}
