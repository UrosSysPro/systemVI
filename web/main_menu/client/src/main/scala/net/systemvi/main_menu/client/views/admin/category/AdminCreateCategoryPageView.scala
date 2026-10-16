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
  /* Set when uploading creates the category, so a later submit updates it
     instead of creating a second one. */
  val createdId = Var(Option.empty[UUID])

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

  /**
   * Picking a file stores the bytes immediately and writes the proxy URL into
   * the form. A category that does not exist yet is created first, so the image
   * can be attached to it right away.
   */
  def uploadImage(bytes: Array[Byte]): scala.concurrent.Future[Unit] =
    val existing = createdId.now()
    val target = existing match {
      case Some(id) => scala.concurrent.Future.successful(Some(id))
      case None =>
        AdminApi
          .createCategory(restaurantId, name.now().trim, optionalText(description.now()), "", Some(parentId))
          .flatMap(_ => AdminApi.latestCategoryId(restaurantId, name.now().trim))
    }

    target.flatMap {
      case Some(id) =>
        createdId.set(Some(id))
        AdminApi.uploadCategoryImage(id, bytes).map(_ => imageUrl.set(uploadedImageUrl("category", id)))
      case None =>
        scala.concurrent.Future.failed(new RuntimeException("Saved the category but could not find its id to attach the image."))
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
        field("Image", imageUpload(imageUrl.signal, uploadImage), Some("Optional")),
      )),
      actions(List(
        submitButton("Create category", busy.signal, onClick --> { _ => submit() }),
        cancelButton("Cancel", Router.navigateTo(AdminCategoriesPage(restaurantId))),
      )),
    )
  )
}
