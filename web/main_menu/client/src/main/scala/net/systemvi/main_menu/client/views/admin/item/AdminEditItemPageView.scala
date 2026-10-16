package net.systemvi.main_menu.client.views.admin.item

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.FormPage
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.views.admin.item.itemForm

import java.util.UUID

/** Edit an existing item. */
def AdminEditItemPageView(restaurantId: UUID, categoryId: UUID, itemId: UUID): HtmlElement = {
  val item = MenuApi.item(itemId).toSignal(Option.empty[ItemDto])

  adminLayout(
    restaurantId,
    AdminCategoryPage(restaurantId, categoryId),
    div(child <-- item.map {
      case None => FormPage.loading("Loading item…")
      case Some(current) =>
        itemForm(
          restaurantId = restaurantId,
          categoryId = categoryId,
          title = "Edit item",
          submitLabel = "Save changes",
          load = Some(current),
          save = (name, imageUrl, description, price) =>
            AdminApi.updateItem(itemId, categoryId, name, imageUrl, description, price),
        )
    }),
  )
}
