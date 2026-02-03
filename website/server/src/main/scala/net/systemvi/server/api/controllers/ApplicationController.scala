package net.systemvi.server.api.controllers

import cats.*
import cats.effect.*
import cats.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import fs2.*
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.persistance.models.Manufacturer
import net.systemvi.server.services.*
import net.systemvi.common.dtos.*
import net.systemvi.server.persistance.models.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*
import java.util.UUID
import scala.util.*

private def getApplicationDto(uuid: UUID)(using context: AppContext[IO]): IO[ApplicationDto] =
  for{
    application <- context.db.applications.get(uuid)
      .map(_.getOrElse(throw Exception("app not found")))

    imageDtos <- context.db.entityImages.get(application.uuid)
      .map(_.map(a => EntityImageDto(a.imageUrl,a.order)))

    appCategoryDto = ApplicationCategory.values
      .find(_.id == application.categoryId)
      .map(c => ApplicationCategoryDto(c.id,c.name))
      .getOrElse(throw Exception("app category not found"))
  } yield ApplicationDto(
    uuid = application.uuid,
    name = application.name,
    codeName = application.codeName,
    description = application.description,
    category = appCategoryDto,
    images = imageDtos,
  )


def applicationController(using context: AppContext[IO]) = HttpRoutes.of[IO]{
  case GET -> Root => for{
    applications <- context.db.applications.get()
    dtos <- applications
      .map(_.uuid)
      .traverse(getApplicationDto)
    response <- Ok(dtos.asJson)
  } yield response

  case GET -> Root / UUIDVar(uuid) => for{
    dto <- getApplicationDto(uuid)
    response <- Ok(dto.asJson)
  } yield response

  case GET -> Root / "categories" / IntVar(categoryId) => for{
    applications <- context.db.applications.get()
    dtos <- applications
      .filter(_.categoryId == categoryId)
      .map(_.uuid)
      .traverse(getApplicationDto)
    response <- Ok(dtos.asJson)
  } yield response
}
