package net.systemvi.main_menu.client.views.admin.table

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.api.AdminApi
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.FormPage.*
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

/** Create a table for a restaurant. */
def AdminCreateTablePageView(restaurantId: UUID): HtmlElement =
  adminLayout(
    restaurantId,
    AdminTablesPage(restaurantId),
    tableForm(
      restaurantId = restaurantId,
      title = "New table",
      initialName = "",
      initialImageUrl = "",
      submitLabel = "Create table",
      save = (name, imageUrl) => AdminApi.createRestaurantTable(restaurantId, name, imageUrl),
    ),
  )

/**
 * Shared body of the create and edit table pages.
 *
 * Returns only the form content: each page wraps it in its own [[adminLayout]].
 * Putting the layout in here would nest a second side nav inside the first.
 */
private[table] def tableForm(
  restaurantId: UUID,
  title: String,
  initialName: String,
  initialImageUrl: String,
  submitLabel: String,
  save: (String, String) => scala.concurrent.Future[Unit],
): HtmlElement = {
  val name = Var(initialName)
  val imageUrl = Var(initialImageUrl)
  val busy = Var(false)
  val error = Var(Option.empty[String])

  def submit(): Unit = {
    busy.set(true)
    error.set(None)
    save(name.now().trim, imageUrl.now().trim).onComplete { result =>
      busy.set(false)
      result.fold(
        failure => error.set(Some(failure.getMessage)),
        _ => Router.pushState(AdminTablesPage(restaurantId)),
      )
    }
  }

  div(
    flexColumn,
    gap.rem(1),
    width.percent(100),
    maxWidth.px(640),
    linkRow("Back to tables", AdminTablesPage(restaurantId)),
    pageTitle(title),
    errorBanner(error.signal),
    section("Table", List(
      field("Name", textInput("e.g. Table 1", name)),
      field("Image URL", textInput("https://…", imageUrl), Some("Optional")),
    )),
    actions(List(
      submitButton(submitLabel, busy.signal, onClick --> { _ => submit() }),
      cancelButton("Cancel", Router.navigateTo(AdminTablesPage(restaurantId))),
    )),
  )
}
