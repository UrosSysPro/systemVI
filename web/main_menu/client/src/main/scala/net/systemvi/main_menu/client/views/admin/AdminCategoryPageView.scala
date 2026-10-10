package net.systemvi.main_menu.client.views.admin

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.{CategoryDto, ItemDto}
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.routes.Pages.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/** Manages a single category: its subcategories and its items. */
def AdminCategoryPageView(restaurantId: UUID, categoryId: UUID): HtmlElement = {
  val reloads = new EventBus[Unit]
  val category = reloadable(reloads, MenuApi.category(categoryId), Option.empty[CategoryDto])
  val items = reloadable(reloads, MenuApi.itemsForCategory(categoryId), List.empty[ItemDto])

  val subName = Var("")
  val subDescription = Var("")
  val subImageUrl = Var("")

  val itemName = Var("")
  val itemDescription = Var("")
  val itemPrice = Var("")
  val itemImageUrl = Var("")
  val editingItemId = Var(Option.empty[UUID])

  def addSubcategory(): Unit =
    AdminApi.createCategory(restaurantId, subName.now(), optionalText(subDescription.now()), subImageUrl.now(), Some(categoryId))
      .onComplete(_ => { subName.set(""); subDescription.set(""); subImageUrl.set(""); reloads.emit(()) })

  def clearItemForm(): Unit = {
    itemName.set("")
    itemDescription.set("")
    itemPrice.set("")
    itemImageUrl.set("")
    editingItemId.set(None)
  }

  def submitItem(): Unit =
    Try(BigDecimal(itemPrice.now())).toOption.foreach { price =>
      val request = editingItemId.now() match {
        case Some(id) => AdminApi.updateItem(id, categoryId, itemName.now(), itemImageUrl.now(), optionalText(itemDescription.now()), price)
        case None     => AdminApi.createItem(categoryId, itemName.now(), itemImageUrl.now(), optionalText(itemDescription.now()), price)
      }
      request.onComplete(_ => { clearItemForm(); reloads.emit(()) })
    }

  def subcategoryRow(sub: CategoryDto): HtmlElement =
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
      span(sub.name),
      actionButton("Delete", onClick --> { _ => AdminApi.deleteCategory(sub.uuid).onComplete(_ => reloads.emit(())) }, "#f5c2c2"),
    )

  def itemRow(item: ItemDto): HtmlElement =
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
        img(src(item.imageUrl), alt(item.name), width.px(48), height.px(48), borderRadius.rem(0.25)),
        div(
          display.flex,
          flexDirection.column,
          span(fontWeight.bold, item.name),
          item.description.fold(emptyNode)(text => span(color("#666666"), fontSize.rem(0.9), text)),
        ),
        span(color("#2e8b57"), s"${item.price} €"),
      ),
      div(
        display.flex,
        flexDirection.row,
        gap.rem(0.5),
        actionButton("Edit", onClick --> { _ =>
          itemName.set(item.name)
          itemDescription.set(item.description.getOrElse(""))
          itemPrice.set(item.price.toString)
          itemImageUrl.set(item.imageUrl)
          editingItemId.set(Some(item.uuid))
        }),
        actionButton("Delete", onClick --> { _ => AdminApi.deleteItem(item.uuid).onComplete(_ => reloads.emit(())) }, "#f5c2c2"),
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
      maxWidth.px(900),
      linkRow("Back to categories", AdminCategoriesPage(restaurantId)),
      h1(fontSize.rem(2), fontWeight.bold, color("#22272d"), child.text <-- category.map(_.map(_.name).getOrElse("Category"))),
      div(
        display.flex,
        flexDirection.column,
        gap.rem(0.5),
        h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), "Subcategories"),
        formRow(List(
          styledInput("Name", subName),
          styledInput("Description", subDescription),
          styledInput("Image URL", subImageUrl),
          actionButton("Add", onClick --> { _ => addSubcategory() }, "#bcd9ff"),
        )),
        child <-- category.map(_.map(_.children).getOrElse(Nil)).map {
          case Nil => div("No subcategories.")
          case list => listColumn(list.map(subcategoryRow))
        },
      ),
      div(
        display.flex,
        flexDirection.column,
        gap.rem(0.5),
        h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), "Items"),
        formRow(List(
          styledInput("Name", itemName),
          styledInput("Description", itemDescription),
          styledInput("Price", itemPrice, 100),
          styledInput("Image URL", itemImageUrl),
          reactiveActionButton(editingItemId.signal.map(id => if id.isDefined then "Save" else "Add"), onClick --> { _ => submitItem() }, "#bcd9ff"),
          actionButton("Cancel", onClick --> { _ => clearItemForm() }),
        )),
        child <-- items.map {
          case Nil => div("No items in this category.")
          case list => listColumn(list.map(itemRow))
        },
      ),
    )
  )
}
