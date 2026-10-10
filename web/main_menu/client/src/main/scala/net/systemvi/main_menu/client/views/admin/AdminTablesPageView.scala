package net.systemvi.main_menu.client.views.admin

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.RestaurantTableDto
import net.systemvi.main_menu.client.api.AdminApi
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.routes.Pages.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

def AdminTablesPageView(restaurantId: UUID): HtmlElement = {
  val name = Var("")
  val imageUrl = Var("")
  val editingId = Var(Option.empty[UUID])
  val reloads = new EventBus[Unit]
  val tables = reloadable(reloads, AdminApi.restaurantTables(restaurantId), List.empty[RestaurantTableDto])

  def clearForm(): Unit = {
    name.set("")
    imageUrl.set("")
    editingId.set(None)
  }

  def submit(): Unit = {
    val request = editingId.now() match {
      case Some(id) => AdminApi.updateRestaurantTable(id, restaurantId, name.now(), imageUrl.now())
      case None     => AdminApi.createRestaurantTable(restaurantId, name.now(), imageUrl.now())
    }
    request.onComplete(_ => { clearForm(); reloads.emit(()) })
  }

  def row(table: RestaurantTableDto): HtmlElement =
    div(
      display.flex,
      flexDirection.row,
      alignItems.center,
      justifyContent.spaceBetween,
      gap.rem(1),
      padding.rem(1),
      borderRadius.rem(0.5),
      backgroundColor("#ffffff"),
      border("1px solid #dddddd"),
      div(
        display.flex,
        flexDirection.row,
        alignItems.center,
        gap.rem(1),
        img(src(table.imageUrl), alt(table.name), width.px(48), height.px(48), borderRadius.rem(0.25)),
        span(fontWeight.bold, table.name),
      ),
      div(
        display.flex,
        flexDirection.row,
        gap.rem(0.5),
        actionButton("Edit", onClick --> { _ =>
          name.set(table.name)
          imageUrl.set(table.imageUrl)
          editingId.set(Some(table.uuid))
        }),
        actionButton("Delete", onClick --> { _ => AdminApi.deleteRestaurantTable(table.uuid).onComplete(_ => reloads.emit(())) }, "#f5c2c2"),
      ),
    )

  adminLayout(
    restaurantId,
    AdminTablesPage(restaurantId),
    div(
      display.flex,
      flexDirection.column,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(900),
      pageTitle("Tables"),
      formRow(List(
        styledInput("Name", name),
        styledInput("Image URL", imageUrl),
        reactiveActionButton(
          editingId.signal.map(id => if id.isDefined then "Save" else "Add"),
          onClick --> { _ => submit() },
          "#bcd9ff",
        ),
        actionButton("Cancel", onClick --> { _ => clearForm() }),
      )),
      child <-- tables.map {
        case Nil => div("No tables yet.")
        case list => listColumn(list.map(row))
      },
    )
  )
}
