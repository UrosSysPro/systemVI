package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.syntax.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.services.CategoryService
import net.systemvi.mainmenu.server.services.CategoryTree
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

import java.util.UUID

def categoryController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val service = CategoryService.create(context.db.categories)
  val logger = context.logger

  HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        categories <- service.getAll()
        response <- Ok(CategoryTree.forest(categories).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) =>
      for {
        categories <- service.getAll()
        response <- CategoryTree.subtree(categories, id) match {
          case Some(category) => Ok(category.asJson)
          case None => NotFound()
        }
      } yield response

    case request @ POST -> Root =>
      case class CreateCategoryDto(name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID])
      given Decoder[CreateCategoryDto] = deriveDecoder
      val write = for {
        dto <- request.as[CreateCategoryDto]
        saved <- service.create(dto.name, dto.description, dto.imageUrl, dto.parentId, dto.restaurantId)
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
      case class UpdateCategoryDto(name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID])
      given Decoder[UpdateCategoryDto] = deriveDecoder
      val write = for {
        dto <- request.as[UpdateCategoryDto]
        updated <- service.update(id, dto.name, dto.description, dto.imageUrl, dto.parentId, dto.restaurantId)
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
