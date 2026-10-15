package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.{CategoryDto, ItemDto, RestaurantTableDto}
import net.systemvi.main_menu.client.api.MenuApi
import net.systemvi.main_menu.client.components.item.ItemCard
import net.systemvi.main_menu.client.components.{listColumn, pageColumn, pageShell, pageTitle}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID

/**
 * The menu as a customer sitting at a table sees it: every category with its
 * items on one page, so nothing stands between them and the item they want.
 *
 * Reached by following the table's link (typically a QR code), which is why it
 * carries the table id into every item link.
 */
def TableMenuPageView(restaurantId: UUID, tableId: UUID): HtmlElement = {
  val categories = MenuApi.categoriesForRestaurant(restaurantId).toSignal(Nil)
  val items = MenuApi.items.toSignal(Nil)
  val table = MenuApi.restaurantTable(tableId).toSignal(Option.empty[RestaurantTableDto])

  def itemsOf(categoryId: UUID, all: List[ItemDto]): List[ItemDto] =
    all.filter(_.categoryId.contains(categoryId))

  /** A category is worth showing only if it holds items itself or in a child. */
  def hasItems(category: CategoryDto, all: List[ItemDto]): Boolean =
    itemsOf(category.uuid, all).nonEmpty || category.children.exists(child => itemsOf(child.uuid, all).nonEmpty)

  def itemList(list: List[ItemDto]): HtmlElement =
    listColumn(list.map(item => ItemCard(item, TableItemPage(restaurantId, tableId, item.uuid))))

  def subcategorySection(sub: CategoryDto, all: List[ItemDto]): HtmlElement =
    div(
      flexColumn,
      gap.rem(0.5),
      h3(fontSize.rem(1.1), fontWeight.bold, color("#22272d"), sub.name),
      itemList(itemsOf(sub.uuid, all)),
    )

  def categorySection(category: CategoryDto, all: List[ItemDto]): HtmlElement =
    val own = itemsOf(category.uuid, all)
    val ownSection = if own.isEmpty then Nil else List(itemList(own))
    val subSections = category.children
      .filter(child => itemsOf(child.uuid, all).nonEmpty)
      .map(sub => subcategorySection(sub, all))

    div(
      flexColumn,
      gap.rem(0.75),
      h2(fontSize.rem(1.4), fontWeight.bold, color("#22272d"), category.name),
      ownSection,
      subSections,
    )

  pageShell(List(
    pageColumn(List(
      pageTitle("Menu"),
      div(color("#6b7280"), child.text <-- table.map(_.map(value => s"Table: ${value.name}").getOrElse(""))),
      div(child <-- categories.combineWith(items).map { case (allCategories, allItems) =>
        allCategories.filter(category => hasItems(category, allItems)) match {
          case Nil     => div("No items on the menu yet.")
          case visible => div(flexColumn, gap.rem(2), visible.map(category => categorySection(category, allItems)))
        }
      }),
    ))
  ))
}
