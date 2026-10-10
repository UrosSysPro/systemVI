package net.systemvi.main_menu.client.api

import com.raquo.laminar.api.L.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import net.systemvi.mainmenu.common.dto.{CategoryDto, ItemDto, RestaurantDto}
import net.systemvi.main_menu.client.utils.Constants
import org.scalajs.dom

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Thenable.Implicits.*

object MenuApi {

  given ExecutionContext = ExecutionContext.global
  given Decoder[ItemDto] = deriveDecoder

  def restaurants: EventStream[List[RestaurantDto]] =
    fetchJson[List[RestaurantDto]](s"${Constants.serverUrl}/restaurants")

  /** Top-level categories of a restaurant, each carrying its nested subcategories. */
  def categoriesForRestaurant(restaurantId: UUID): EventStream[List[CategoryDto]] =
    fetchJson[List[CategoryDto]](s"${Constants.serverUrl}/restaurants/$restaurantId/categories")

  def category(categoryId: UUID): EventStream[Option[CategoryDto]] =
    fetchJsonOption[CategoryDto](s"${Constants.serverUrl}/category/$categoryId")

  def itemsForCategory(categoryId: UUID): EventStream[List[ItemDto]] =
    fetchJson[List[ItemDto]](s"${Constants.serverUrl}/items/category/$categoryId")

  private def fetchJson[A](url: String)(using decoder: Decoder[A]): EventStream[A] =
    EventStream.fromFuture(
      dom.fetch(url).toFuture
        .flatMap(_.text().toFuture)
        .map(body => decode[A](body).fold(error => throw error, identity))
    )

  private def fetchJsonOption[A](url: String)(using decoder: Decoder[A]): EventStream[Option[A]] =
    EventStream.fromFuture(
      dom.fetch(url).toFuture.flatMap { response =>
        if response.ok then
          response.text().toFuture.map(body => decode[A](body).toOption)
        else
          Future.successful(None)
      }
    )
}
