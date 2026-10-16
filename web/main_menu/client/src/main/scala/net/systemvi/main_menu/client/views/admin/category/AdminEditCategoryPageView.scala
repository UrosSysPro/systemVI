package net.systemvi.main_menu.client.views.admin.category

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.main_menu.client.api.{AdminApi, MenuApi}
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.FormPage
import net.systemvi.main_menu.client.components.FormPage.*
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

/** Edit an existing category's name, description and image. */
def AdminEditCategoryPageView(restaurantId: UUID, categoryId: UUID): HtmlElement = {
  val name = Var("")
  val description = Var("")
  val imageUrl = Var("")
  val busy = Var(false)
  val error = Var(Option.empty[String])

  val category = MenuApi
    .category(categoryId)
    .toSignal(Option.empty[CategoryDto])

  def backPage(parentId: Option[UUID]): Page =
    parentId match {
      case Some(parent) => AdminCategoryPage(restaurantId, parent)
      case None         => AdminCategoriesPage(restaurantId)
    }

  def submit(parentId: Option[UUID]): Unit = {
    busy.set(true)
    error.set(None)
    AdminApi
      .updateCategory(categoryId, restaurantId, name.now().trim, optionalText(description.now()), imageUrl.now().trim, parentId)
      .onComplete { result =>
        busy.set(false)
        result.fold(
          failure => error.set(Some(failure.getMessage)),
          _ => Router.pushState(AdminCategoryPage(restaurantId, categoryId)),
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
      child <-- category.map {
        case None => FormPage.loading("Loading category…")
        case Some(current) =>
          val parentPage = backPage(current.parentId)
          div(
            flexColumn,
            gap.rem(1),
            // Seeding runs on mount, so it happens once and has a real Owner.
            onMountCallback { _ =>
              name.set(current.name)
              description.set(current.description.getOrElse(""))
              imageUrl.set(current.imageUrl)
            },
            linkRow("Back", parentPage),
            pageTitle("Edit category"),
            errorBanner(error.signal),
            section("Category", List(
              field("Name", textInput("e.g. Starters", name)),
              field("Description", textInputArea("Optional", description), Some("Optional")),
              field("Image URL", textInput("https://…", imageUrl), Some("Optional")),
            )),
            actions(List(
              submitButton("Save changes", busy.signal, onClick --> { _ => submit(current.parentId) }),
              cancelButton("Cancel", Router.navigateTo(parentPage)),
            )),
          )
      },
    )
  )
}
