package net.systemvi.main_menu.client.views.admin.item

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.main_menu.client.api.AdminApi
import net.systemvi.main_menu.client.components.*
import net.systemvi.main_menu.client.components.FormPage.*
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.styles.CSSProps.*

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

/** Create an item inside a category. */
def AdminCreateItemPageView(restaurantId: UUID, categoryId: UUID): HtmlElement =
  adminLayout(
    restaurantId,
    AdminCategoryPage(restaurantId, categoryId),
    itemForm(
      restaurantId = restaurantId,
      categoryId = categoryId,
      title = "New item",
      submitLabel = "Create item",
      load = None,
      save = (name, imageUrl, description, price) => AdminApi.createItem(categoryId, name, imageUrl, description, price),
    ),
  )

/**
 * Shared body of the create and edit item pages.
 *
 * Returns only the form content: each page wraps it in its own [[adminLayout]].
 * Putting the layout in here would nest a second side nav inside the first.
 */
private[item] def itemForm(
  restaurantId: UUID,
  categoryId: UUID,
  title: String,
  submitLabel: String,
  load: Option[ItemDto],
  save: (String, String, Option[String], BigDecimal) => scala.concurrent.Future[Unit],
): HtmlElement = {
  val name = Var("")
  val description = Var("")
  val imageUrl = Var("")
  val price = Var("")
  val busy = Var(false)
  val error = Var(Option.empty[String])

  def submit(): Unit =
    Try(BigDecimal(price.now().trim)).toOption.filter(_.signum >= 0) match {
      case None =>
        error.set(Some("Price must be a number, for example 12.50."))
      case Some(parsedPrice) =>
        busy.set(true)
        error.set(None)
        save(name.now().trim, imageUrl.now().trim, optionalText(description.now()), parsedPrice).onComplete { result =>
          busy.set(false)
          result.fold(
            failure => error.set(Some(failure.getMessage)),
            _ => Router.pushState(AdminCategoryPage(restaurantId, categoryId)),
          )
        }
    }

  /**
   * Picking a file stores the bytes immediately and writes the proxy URL into
   * the form, so submitting the form afterwards keeps the two in step. Creating
   * an item uploads against the id the server just returned, which is why the
   * upload can happen before the item has been saved once.
   */
  def uploadImage(bytes: Array[Byte]): scala.concurrent.Future[Unit] =
    load match {
      case Some(current) =>
        AdminApi.uploadItemImage(current.uuid, bytes).map { _ =>
          imageUrl.set(uploadedImageUrl("item", current.uuid))
        }
      case None =>
        Try(BigDecimal(price.now().trim)).toOption.filter(_.signum >= 0) match {
          case None =>
            scala.concurrent.Future.failed(new RuntimeException("Enter a valid price before uploading an image."))
          case Some(parsedPrice) =>
            AdminApi
              .createItem(categoryId, name.now().trim, imageUrl.now().trim, optionalText(description.now()), parsedPrice)
              .flatMap { _ =>
                // The create endpoint returns the new id as text, so reload the
                // category's items to find it; simpler than a second endpoint.
                AdminApi.latestItemId(categoryId, name.now().trim).flatMap {
                  case Some(id) => AdminApi.uploadItemImage(id, bytes).map(_ => imageUrl.set(uploadedImageUrl("item", id)))
                  case None     => scala.concurrent.Future.failed(new RuntimeException("Saved the item but could not find its id to attach the image."))
                }
              }
        }
    }

  div(
    flexColumn,
    gap.rem(1),
    width.percent(100),
    maxWidth.px(640),
    load.fold(emptyMod)(current =>
      // Seeding runs on mount, so it happens once and has a real Owner.
      onMountCallback { _ =>
        name.set(current.name)
        description.set(current.description.getOrElse(""))
        imageUrl.set(current.imageUrl)
        price.set(current.price.toString)
      }
    ),
    linkRow("Back to category", AdminCategoryPage(restaurantId, categoryId)),
    pageTitle(title),
    errorBanner(error.signal),
    section("Item", List(
      field("Name", textInput("e.g. Margherita", name)),
      field("Description", textInputArea("Optional", description), Some("Optional")),
      field("Price", textInput("e.g. 12.50", price), Some("In euros, numbers only")),
      field("Image", imageUpload(imageUrl.signal, uploadImage), Some("Optional")),
    )),
    actions(List(
      submitButton(submitLabel, busy.signal, onClick --> { _ => submit() }),
      cancelButton("Cancel", Router.navigateTo(AdminCategoryPage(restaurantId, categoryId))),
    )),
  )
}
