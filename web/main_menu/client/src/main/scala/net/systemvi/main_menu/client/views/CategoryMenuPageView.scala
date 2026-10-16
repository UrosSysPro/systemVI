package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.category.CategoryCard
import net.systemvi.main_menu.client.components.item.ItemCard
import net.systemvi.main_menu.client.components.{listColumn, pageColumn, pageShell}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID

/** A category page: subcategories on top, items below. */
def CategoryMenuPageView(restaurantId: UUID, categoryId: UUID): HtmlElement =
  categoryMenu(restaurantId, categoryId, categoryId)

/**
 * A subcategory page: structurally identical to a category page. Categories only
 * go two levels deep in this app, so a subcategory never has children of its own.
 */
def SubcategoryMenuPageView(restaurantId: UUID, categoryId: UUID, subcategoryId: UUID): HtmlElement =
  categoryMenu(restaurantId, categoryId, subcategoryId)

private def categoryMenu(restaurantId: UUID, parentCategoryId: UUID, categoryId: UUID): HtmlElement = {
  val category = MenuApi.category(categoryId).toSignal(Option.empty[CategoryDto])
  val items = MenuApi.itemsForCategory(categoryId).toSignal(Nil)

  pageShell(List(
    pageColumn(List(
      h1(fontSize.rem(2), fontWeight.bold, color("#22272d"), child.text <-- category.map(_.map(_.name).getOrElse("Category"))),
      div(child <-- category.map {
        case Some(value) if value.children.nonEmpty =>
          div(
            flexColumn,
            gap.rem(0.5),
            h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), "Subcategories"),
            listColumn(value.children.map(child => CategoryCard(child, SubcategoryMenuPage(restaurantId, parentCategoryId, child.uuid)))),
          )
        case _ => emptyNode
      }),
      div(child <-- items.map {
        case Nil => div("No items in this category.")
        case list =>
          div(
            flexColumn,
            gap.rem(0.5),
            h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), "Items"),
            listColumn(list.map(item => ItemCard(item, ItemPage(restaurantId, item.uuid)))),
          )
      }),
    ))
  ))
}
