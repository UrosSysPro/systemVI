package net.systemvi.main_menu.client.api

import com.raquo.laminar.api.L.*
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.parser.decode
import io.circe.syntax.*
import net.systemvi.mainmenu.common.dto.RestaurantTableDto
import net.systemvi.main_menu.client.utils.Constants
import org.scalajs.dom
import org.scalajs.dom.{Headers, HttpMethod, RequestInit}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Thenable.Implicits.*

/** Write operations (create/update/delete) plus admin-only reads. */
object AdminApi {

  given ExecutionContext = ExecutionContext.global

  private case class RestaurantTableBody(name: String, imageUrl: String, restaurantId: UUID)
  private case class CategoryBody(name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID])
  private case class ItemBody(name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID])

  private given Encoder[RestaurantTableBody] = deriveEncoder
  private given Encoder[CategoryBody] = deriveEncoder
  private given Encoder[ItemBody] = deriveEncoder

  def restaurantTables(restaurantId: UUID): EventStream[List[RestaurantTableDto]] =
    EventStream.fromFuture(
      dom.fetch(s"${Constants.serverUrl}/restaurants/$restaurantId/tables").toFuture
        .flatMap(_.text().toFuture)
        .map(body => decode[List[RestaurantTableDto]](body).fold(error => throw error, identity))
    )

  def createRestaurantTable(restaurantId: UUID, name: String, imageUrl: String): Future[Unit] =
    send(HttpMethod.POST, s"${Constants.serverUrl}/restaurant-tables", Some(RestaurantTableBody(name, imageUrl, restaurantId).asJson.noSpaces))

  def updateRestaurantTable(id: UUID, restaurantId: UUID, name: String, imageUrl: String): Future[Unit] =
    send(HttpMethod.PUT, s"${Constants.serverUrl}/restaurant-tables/$id", Some(RestaurantTableBody(name, imageUrl, restaurantId).asJson.noSpaces))

  def deleteRestaurantTable(id: UUID): Future[Unit] =
    send(HttpMethod.DELETE, s"${Constants.serverUrl}/restaurant-tables/$id", None)

  def createCategory(restaurantId: UUID, name: String, description: Option[String], imageUrl: String, parentId: Option[UUID]): Future[Unit] =
    send(HttpMethod.POST, s"${Constants.serverUrl}/category", Some(CategoryBody(name, description, imageUrl, parentId, Some(restaurantId)).asJson.noSpaces))

  def updateCategory(id: UUID, restaurantId: UUID, name: String, description: Option[String], imageUrl: String, parentId: Option[UUID]): Future[Unit] =
    send(HttpMethod.PUT, s"${Constants.serverUrl}/category/$id", Some(CategoryBody(name, description, imageUrl, parentId, Some(restaurantId)).asJson.noSpaces))

  def deleteCategory(id: UUID): Future[Unit] =
    send(HttpMethod.DELETE, s"${Constants.serverUrl}/category/$id", None)

  def createItem(categoryId: UUID, name: String, imageUrl: String, description: Option[String], price: BigDecimal): Future[Unit] =
    send(HttpMethod.POST, s"${Constants.serverUrl}/items", Some(ItemBody(name, imageUrl, description, price, Some(categoryId)).asJson.noSpaces))

  def updateItem(id: UUID, categoryId: UUID, name: String, imageUrl: String, description: Option[String], price: BigDecimal): Future[Unit] =
    send(HttpMethod.PUT, s"${Constants.serverUrl}/items/$id", Some(ItemBody(name, imageUrl, description, price, Some(categoryId)).asJson.noSpaces))

  def deleteItem(id: UUID): Future[Unit] =
    send(HttpMethod.DELETE, s"${Constants.serverUrl}/items/$id", None)

  private def send(method: HttpMethod, url: String, body: Option[String]): Future[Unit] =
    dom.fetch(url, requestInit(method, body)).toFuture.map { response =>
      if response.ok then () else throw new RuntimeException(s"$method $url failed with status ${response.status}")
    }

  private def requestInit(method: HttpMethod, body: Option[String]): RequestInit = {
    val init = new RequestInit {}
    init.method = method
    body.foreach { json =>
      init.body = json
      val headers = Headers()
      headers.append("Content-Type", "application/json")
      init.headers = headers
    }
    init
  }
}
