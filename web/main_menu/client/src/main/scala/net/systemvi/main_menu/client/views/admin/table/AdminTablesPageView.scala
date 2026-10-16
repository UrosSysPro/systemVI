package net.systemvi.main_menu.client.views.admin.table

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.RestaurantTableDto
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

/** All tables of a restaurant, each linking to its table menu. */
def AdminTablesPageView(restaurantId: UUID): HtmlElement = {
  val reloads = new EventBus[Unit]
  val tables = reloadable(reloads, AdminApi.restaurantTables(restaurantId), List.empty[RestaurantTableDto])

  def row(table: RestaurantTableDto): HtmlElement =
    div(
      flexRow,
      alignItems.center,
      justifyContent.spaceBetween,
      gap.rem(1),
      padding.rem(1),
      cardSurface,
      div(
        flexRow,
        alignItems.center,
        gap.rem(1),
        img(
          src := table.imageUrl,
          alt := table.name,
          width.px(48),
          height.px(48),
          aspect := "1 / 1",
          objectFit := "cover",
          borderRadius.rem(0.5),
          backgroundColor("#f0f1f3"),
        ),
        span(fontWeight.bold, table.name),
      ),
      div(
        flexRow,
        gap.rem(0.5),
        actionButton("Open menu", onClick --> { _ => Router.pushState(TableMenuPage(restaurantId, table.uuid)) }),
        actionButton("Edit", onClick --> { _ => Router.pushState(AdminEditTablePage(restaurantId, table.uuid)) }, "#bcd9ff"),
        actionButton(
          "Delete",
          onClick --> { _ => AdminApi.deleteRestaurantTable(table.uuid).onComplete(_ => reloads.emit(())) },
          "#f5c2c2",
        ),
      ),
    )

  adminLayout(
    restaurantId,
    AdminTablesPage(restaurantId),
    div(
      flexColumn,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(900),
      div(
        flexRow,
        alignItems.center,
        justifyContent.spaceBetween,
        gap.rem(1),
        pageTitle("Tables"),
        actionButton(
          "New table",
          onClick --> { _ => Router.pushState(AdminCreateTablePage(restaurantId)) },
          "#bcd9ff",
        ),
      ),
      div(color("#6b7280"), fontSize.rem(0.9), "Each table has its own menu URL, which is what a table's QR code should point at."),
      child <-- tables.map {
        case Nil     => div("No tables yet.")
        case list    => listColumn(list.map(row))
      },
    )
  )
}
