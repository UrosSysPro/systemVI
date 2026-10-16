package net.systemvi.main_menu.client.views.admin.category

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.api.AdminApi
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.FormPage
import net.systemvi.main_menu.client.components.FormPage.*
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

/** Create a category. Used for both top-level and subcategories. */
def AdminCreateCategoryPageView(restaurantId: UUID, parentId: UUID): HtmlElement =
  categoryForm(restaurantId, parentId, "New category")

/**
 * Create a top-level category.
 *
 * The route carries a parent anyway, because the server's `CategoryDto` needs
 * one and `Category.restaurantId` has no category page of its own to hang a
 * parentless route off. The restaurant id is passed as that placeholder.
 */
def AdminCreateTopLevelCategoryPageView(restaurantId: UUID): HtmlElement =
  categoryForm(restaurantId, restaurantId, "New category")

private def categoryForm(restaurantId: UUID, parentId: UUID, title: String): HtmlElement = {
  val name = Var("")
  val description = Var("")
  val imageUrl = Var("")
  val busy = Var(false)
  val error = Var(Option.empty[String])

  def submit(): Unit = {
    busy.set(true)
    error.set(None)
    AdminApi
      .createCategory(restaurantId, name.now().trim, optionalText(description.now()), imageUrl.now().trim, Some(parentId))
      .onComplete { result =>
        busy.set(false)
        result.fold(
          failure => error.set(Some(failure.getMessage)),
          _ => Router.pushState(AdminCategoryPage(restaurantId, parentId)),
        )
      }
  }

  adminLayout(
    restaurantId,
    AdminCategoriesPage(restaurantId),
    div(
      flexColumn,
      gap.rem(1),
      width.percent(100),
      maxWidth.px(640),
      linkRow("Back to categories", AdminCategoriesPage(restaurantId)),
      pageTitle(title),
      errorBanner(error.signal),
      section("Category", List(
        field("Name", textInput("e.g. Starters", name)),
        field("Description", textInputArea("Optional", description), Some("Optional")),
        field("Image URL", textInput("https://…", imageUrl), Some("Optional")),
      )),
      actions(List(
        submitButton("Create category", busy.signal, onClick --> { _ => submit() }),
        cancelButton("Cancel", Router.navigateTo(AdminCategoriesPage(restaurantId))),
      )),
    )
  )
}
