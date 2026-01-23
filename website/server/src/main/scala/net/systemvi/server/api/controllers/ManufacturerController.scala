package net.systemvi.server.api.controllers

import cats.*
import cats.effect.*
import cats.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import fs2.*
import net.systemvi.server.persistance.contexts.ApplicationContext
import net.systemvi.server.persistance.models.Manufacturer
import net.systemvi.server.services.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*

import java.util.UUID

def manufacturerController(using context:ApplicationContext[IO]):HttpRoutes[IO] = HttpRoutes.of[IO] {
  case GET -> Root =>
    val db = context.db
    val logger = context.logger
    (for {
      _ <- logger.info("before read")
      manufacturers <- db.manufacturers.get()
      _ <- logger.info("successful read")
      response <- Ok(manufacturers.asJson)
    } yield response).attempt.flatMap {
      case Right(response) => IO {
        response
      }
      case Left(error) => logger.error(error.getMessage).flatMap(_ => InternalServerError("hello"))
    }

  case GET -> Root / UUIDVar(id) => for {
    manufacturer <- context.db.manufacturers.get(id)
    response <- manufacturer match {
      case Some(m) => Ok(manufacturer.asJson)
      case None => NotFound()
    }
  } yield response

  case request@POST -> Root => {
    case class ManufacturerDto(name: String)
    val write = for {
      dto <- request.as[ManufacturerDto]
      model = Manufacturer(UUID.randomUUID(), dto.name)
      save <- context.db.manufacturers.add(model)
      response <- Ok(save.toString)
    } yield response

    for {
      result <- write.attempt
      response <- result match {
        case Right(result) => IO {
          result
        }
        case Left(exception) => exception match {
          case e: ParsingFailure => BadRequest(e.message)
          case e => BadRequest(e.getMessage)
        }
      }
    } yield response
  }

  case PUT -> Root => Ok("update manufacturer")

  case DELETE -> Root => Ok("delete manufacturer")
}
