package net.systemvi.main_menu.client.views.admin.category

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.category.CategoryCard
import net.systemvi.main_menu.client.components.item.ItemCard
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

/** All top-level categories of a restaurant, as a wrapping grid. */
def AdminCategoriesPageView(restaurantId: UUID): HtmlElement = {
  val reloads = new EventBus[Unit]
  val categories = reloadable(reloads, MenuApi.categoriesForRestaurant(restaurantId), List.empty[CategoryDto])

  adminLayout(
    restaurantId,
    AdminCategoriesPage(restaurantId),
    div(
      flexColumn,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(1000),
      div(
        flexRow,
        alignItems.center,
        justifyContent.spaceBetween,
        gap.rem(1),
        pageTitle("Categories"),
        actionButton(
          "New category",
          onClick --> { _ => Router.pushState(AdminCreateCategoryPage(restaurantId, restaurantId)) },
          "#bcd9ff",
        ),
      ),
      div(color("#6b7280"), fontSize.rem(0.9), "Creating a category here makes it top level. Open a category to add subcategories to it."),
      div(child <-- categories.map {
        case Nil => div("No categories yet.")
        case list =>
          div(
            display.grid,
            gridTemplateColumns := "repeat(auto-fill, minmax(320px, 1fr))",
            gap.rem(0.75),
            list.map(category => CategoryCard(category, AdminCategoryPage(restaurantId, category.uuid))),
          )      }),
    )
  )
}

/** A single category: its subcategories and its items, both managed from here. */
def AdminCategoryPageView(restaurantId: UUID, categoryId: UUID): HtmlElement = {
  val reloads = new EventBus[Unit]
  val category = reloadable(reloads, MenuApi.category(categoryId), Option.empty[CategoryDto])

  def backPage(parentId: Option[UUID]): Page =
    parentId match {
      case Some(parent) => AdminCategoryPage(restaurantId, parent)
      case None         => AdminCategoriesPage(restaurantId)
    }

  def header(current: CategoryDto, parentName: Signal[String]): HtmlElement =
    div(
      flexRow,
      alignItems.center,
      justifyContent.spaceBetween,
      gap.rem(1),
      div(
        flexColumn,
        gap.rem(0.25),
        h1(fontSize.rem(2), fontWeight.bold, color("#22272d"), current.name),
        span(fontSize.rem(0.9), color("#6b7280"), child.text <-- parentName),
      ),
      div(
        flexRow,
        gap.rem(0.5),
        actionButton("Edit", onClick --> { _ => Router.pushState(AdminEditCategoryPage(restaurantId, current.uuid)) }),
        actionButton(
          "Delete",
          onClick --> { _ =>
            AdminApi.deleteCategory(current.uuid).onComplete { result =>
              result.fold(
                _ => (),
                _ => Router.pushState(backPage(current.parentId)),
              )
            }
          },
          "#f5c2c2",
        ),
      ),
    )

  def sectionTitle(title: String, create: Page): HtmlElement =
    div(
      flexRow,
      alignItems.center,
      justifyContent.spaceBetween,
      gap.rem(1),
      h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), title),
      actionButton("Add", onClick --> { _ => Router.pushState(create) }, "#bcd9ff"),
    )

  adminLayout(
    restaurantId,
    AdminCategoriesPage(restaurantId),
    div(
      flexColumn,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(900),
      /* `current` is also what `category` starts out as, so the back link's label
         stays correct when the tree is reopened with a stale page. */
      child <-- category.map {
        case None => FormPage.loading("Loading category…")
        case Some(current) =>
          val parentName =
            if current.parentId.isEmpty then Val("Top level in this restaurant")
            else
              current.parentId
                .map(parentId => MenuApi.category(parentId).toSignal(Option.empty[CategoryDto]).map(_.map(_.name).getOrElse("Parent category")))
                .get

          div(
            flexColumn,
            gap.rem(1),
            linkRow(if current.parentId.isEmpty then "Back to categories" else "Back to parent category", backPage(current.parentId)),
            header(current, parentName),
            current.description.fold(emptyNode)(text => p(color("#4b5563"), lineHeight := "1.5", text)),
            div(
              flexColumn,
              gap.rem(0.5),
              sectionTitle("Subcategories", AdminCreateCategoryPage(restaurantId, current.uuid)),
              if current.children.isEmpty then div(color("#6b7280"), "No subcategories.")
              else listColumn(current.children.map(child => CategoryCard(child, AdminCategoryPage(restaurantId, child.uuid)))),
            ),
            div(child <-- MenuApi.itemsForCategory(current.uuid).map { items =>
              div(
                flexColumn,
                gap.rem(0.5),
                sectionTitle("Items", AdminCreateItemPage(restaurantId, current.uuid)),
                if items.isEmpty then div(color("#6b7280"), "No items in this category.")
                else listColumn(items.map(item => ItemCard(item, AdminEditItemPage(restaurantId, current.uuid, item.uuid)))),
              )
            }),
          )
      },
    )
  )
}
