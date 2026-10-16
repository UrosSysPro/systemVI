package net.systemvi.main_menu.client.views.admin.table

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.RestaurantTableDto
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.FormPage
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.views.admin.table.tableForm

import java.util.UUID

/** Edit an existing table. */
def AdminEditTablePageView(restaurantId: UUID, tableId: UUID): HtmlElement = {
  val table = MenuApi.restaurantTable(tableId).toSignal(Option.empty[RestaurantTableDto])

  adminLayout(
    restaurantId,
    AdminTablesPage(restaurantId),
    div(child <-- table.map {
      case None => FormPage.loading("Loading table…")
      case Some(current) =>
        tableForm(
          restaurantId = restaurantId,
          title = "Edit table",
          initialName = current.name,
          initialImageUrl = current.imageUrl,
          submitLabel = "Save changes",
          save = (name, imageUrl) => AdminApi.updateRestaurantTable(tableId, restaurantId, name, imageUrl),
        )
    }),
  )
}
