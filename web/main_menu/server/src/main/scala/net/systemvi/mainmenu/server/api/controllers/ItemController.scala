package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import net.systemvi.mainmenu.common.dto.ItemDto
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.persistance.models.Item
import net.systemvi.mainmenu.server.services.ItemService
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

import java.util.UUID

def itemController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val service = ItemService.create(context.db.items)
  val logger = context.logger

  def toDto(item: Item): ItemDto =
    ItemDto(item.uuid, item.name, item.imageUrl, item.description, item.price, item.categoryId)

  HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        items <- service.getAll()
        response <- Ok(items.map(toDto).asJson)
      } yield response

    case GET -> Root / "category" / UUIDVar(categoryId) =>
      for {
        items <- service.getByCategory(categoryId)
        response <- Ok(items.map(toDto).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) =>
      for {
        item <- service.get(id)
        response <- item match {
          case Some(i) => Ok(toDto(i).asJson)
          case None => NotFound()
        }
      } yield response

    case request @ POST -> Root =>
      case class CreateItemDto(name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID])
      val write = for {
        dto <- request.as[CreateItemDto]
        saved <- service.create(dto.name, dto.imageUrl, dto.description, dto.price, dto.categoryId)
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
      case class UpdateItemDto(name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID])
      val write = for {
        dto <- request.as[UpdateItemDto]
        updated <- service.update(id, dto.name, dto.imageUrl, dto.description, dto.price, dto.categoryId)
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

    // Raw image bytes in the body; served back out by /image/item/{id}.
    case request @ PUT -> Root / UUIDVar(id) / "image" =>
      val write = for {
        bytes <- readImageBody(request)
        response <- bytes match {
          case Left(message) => BadRequest(message)
          case Right(image) =>
            context.db.items.updateImage(id, image).flatMap {
              case updated if updated > 0 => Ok("image stored")
              case _                      => NotFound()
            }
        }
      } yield response

      write.attempt.flatMap {
        case Right(response) => IO.pure(response)
        case Left(error) =>
          logger.error(error)(s"Failed to store item image $id") *> InternalServerError(error.getMessage)
      }
  }
}
