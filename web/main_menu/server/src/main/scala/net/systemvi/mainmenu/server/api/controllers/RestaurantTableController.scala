package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import net.systemvi.mainmenu.common.dto.RestaurantTableDto
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.persistance.models.RestaurantTable
import net.systemvi.mainmenu.server.services.RestaurantTableService
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

import java.util.UUID

def restaurantTableController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val service = RestaurantTableService.create(context.db.restaurantTables)
  val logger = context.logger

  def toDto(table: RestaurantTable): RestaurantTableDto =
    RestaurantTableDto(table.uuid, table.name, table.imageUrl, table.restaurantId)

  HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        tables <- service.getAll()
        response <- Ok(tables.map(toDto).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) =>
      for {
        table <- service.get(id)
        response <- table match {
          case Some(t) => Ok(toDto(t).asJson)
          case None => NotFound()
        }
      } yield response

    case request @ POST -> Root =>
      case class CreateRestaurantTableDto(name: String, imageUrl: String, restaurantId: UUID)
      val write = for {
        dto <- request.as[CreateRestaurantTableDto]
        saved <- service.create(dto.name, dto.imageUrl, dto.restaurantId)
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
      case class UpdateRestaurantTableDto(name: String, imageUrl: String, restaurantId: UUID)
      val write = for {
        dto <- request.as[UpdateRestaurantTableDto]
        updated <- service.update(id, dto.name, dto.imageUrl, dto.restaurantId)
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
