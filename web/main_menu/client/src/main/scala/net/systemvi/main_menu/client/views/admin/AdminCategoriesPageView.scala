package net.systemvi.main_menu.client.views.admin

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

/** Top-level categories shown as a wrapping grid, with create/edit/delete. */
def AdminCategoriesPageView(restaurantId: UUID): HtmlElement = {
  val name = Var("")
  val description = Var("")
  val imageUrl = Var("")
  val editingId = Var(Option.empty[UUID])
  val reloads = new EventBus[Unit]
  val categories = reloadable(reloads, MenuApi.categoriesForRestaurant(restaurantId), List.empty[CategoryDto])

  def clearForm(): Unit = {
    name.set("")
    description.set("")
    imageUrl.set("")
    editingId.set(None)
  }

  def submit(): Unit = {
    val request = editingId.now() match {
      case Some(id) => AdminApi.updateCategory(id, restaurantId, name.now(), optionalText(description.now()), imageUrl.now(), None)
      case None     => AdminApi.createCategory(restaurantId, name.now(), optionalText(description.now()), imageUrl.now(), None)
    }
    request.onComplete(_ => { clearForm(); reloads.emit(()) })
  }

  def card(category: CategoryDto): HtmlElement =
    div(
      display.flex,
      flexDirection.column,
      gap.rem(0.5),
      width.px(220),
      padding.rem(1),
      borderRadius.rem(0.5),
      backgroundColor("#ffffff"),
      border("1px solid #dddddd"),
      img(src(category.imageUrl), alt(category.name), width.percent(100), height.px(120), borderRadius.rem(0.25)),
      a(
        Router.navigateTo(AdminCategoryPage(restaurantId, category.uuid)),
        fontWeight.bold,
        color("#22272d"),
        textDecoration("none"),
        cursor.pointer,
        category.name,
      ),
      category.description.fold(emptyNode)(text => span(color("#666666"), fontSize.rem(0.9), text)),
      div(
        display.flex,
        flexDirection.row,
        gap.rem(0.5),
        actionButton("Edit", onClick --> { _ =>
          name.set(category.name)
          description.set(category.description.getOrElse(""))
          imageUrl.set(category.imageUrl)
          editingId.set(Some(category.uuid))
        }),
        actionButton("Delete", onClick --> { _ => AdminApi.deleteCategory(category.uuid).onComplete(_ => reloads.emit(())) }, "#f5c2c2"),
      ),
    )

  adminLayout(
    restaurantId,
    AdminCategoriesPage(restaurantId),
    div(
      display.flex,
      flexDirection.column,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(1000),
      pageTitle("Categories"),
      formRow(List(
        styledInput("Name", name),
        styledInput("Description", description),
        styledInput("Image URL", imageUrl),
        reactiveActionButton(editingId.signal.map(id => if id.isDefined then "Save" else "Add"), onClick --> { _ => submit() }, "#bcd9ff"),
        actionButton("Cancel", onClick --> { _ => clearForm() }),
      )),
      child <-- categories.map {
        case Nil => div("No categories yet.")
        case list => div(display.flex, flexDirection.row, flexWrap.wrap, gap.rem(1), list.map(card))
      },
    )
  )
}
