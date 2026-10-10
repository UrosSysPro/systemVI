package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.syntax.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import net.systemvi.mainmenu.common.dto.{RestaurantDto, RestaurantTableDto}
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.persistance.models.Restaurant
import net.systemvi.mainmenu.server.services.{CategoryService, CategoryTree, RestaurantService, RestaurantTableService}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

import java.util.UUID

def restaurantController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val service = RestaurantService.create(context.db.restaurants)
  val categories = CategoryService.create(context.db.categories)
  val tables = RestaurantTableService.create(context.db.restaurantTables)
  val logger = context.logger

  def toDto(restaurant: Restaurant): RestaurantDto =
    RestaurantDto(restaurant.uuid, restaurant.name, restaurant.description, restaurant.address, restaurant.imageUrl)

  HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        restaurants <- service.getAll()
        response <- Ok(restaurants.map(toDto).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) / "categories" =>
      for {
        restaurantCategories <- categories.getByRestaurant(id)
        response <- Ok(CategoryTree.forest(restaurantCategories).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) / "tables" =>
      for {
        restaurantTables <- tables.getByRestaurant(id)
        response <- Ok(restaurantTables.map(t => RestaurantTableDto(t.uuid, t.name, t.imageUrl, t.restaurantId)).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) =>
      for {
        restaurant <- service.get(id)
        response <- restaurant match {
          case Some(r) => Ok(toDto(r).asJson)
          case None => NotFound()
        }
      } yield response

    case request @ POST -> Root =>
      case class CreateRestaurantDto(name: String, description: Option[String], address: String, imageUrl: String)
      given Decoder[CreateRestaurantDto] = deriveDecoder
      val write = for {
        dto <- request.as[CreateRestaurantDto]
        saved <- service.create(dto.name, dto.description, dto.address, dto.imageUrl)
        response <- Ok(saved.toString)
      } yield response

      for {
        result <- write.attempt
        response <- result match {
          case Right(r) => IO.pure(r)
          case Left(e) => BadRequest(e.getMessage)
        }
      } yield response

    case request @ PUT -> Root / UUIDVar(id) =>
      case class UpdateRestaurantDto(name: String, description: Option[String], address: String, imageUrl: String)
      given Decoder[UpdateRestaurantDto] = deriveDecoder
      val write = for {
        dto <- request.as[UpdateRestaurantDto]
        updated <- service.update(id, dto.name, dto.description, dto.address, dto.imageUrl)
        response <- if (updated > 0) Ok(updated.toString) else NotFound()
      } yield response

      for {
        result <- write.attempt
        response <- result match {
          case Right(r) => IO.pure(r)
          case Left(e) => BadRequest(e.getMessage)
        }
      } yield response

    case DELETE -> Root / UUIDVar(id) =>
      for {
        deleted <- service.delete(id)
        response <- if (deleted > 0) Ok(deleted.toString) else NotFound()
      } yield response
  }
}
