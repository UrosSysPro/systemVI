package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.services.MenuService
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*

import java.util.UUID

def menuController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val service = MenuService.create(context.db.menus)
  val logger = context.logger

  HttpRoutes.of[IO] {
    case GET -> Root =>
      for {
        menus <- service.getAll()
        response <- Ok(menus.map(m =>
          net.systemvi.mainmenu.common.dto.MenuDto(m.uuid, m.name, m.description)
        ).asJson)
      } yield response

    case GET -> Root / UUIDVar(id) =>
      for {
        menu <- service.get(id)
        response <- menu match {
          case Some(m) => Ok(net.systemvi.mainmenu.common.dto.MenuDto(m.uuid, m.name, m.description).asJson)
          case None => NotFound()
        }
      } yield response

    case request @ POST -> Root =>
      case class CreateMenuDto(name: String, description: Option[String])
      val write = for {
        dto <- request.as[CreateMenuDto]
        saved <- service.create(dto.name, dto.description)
        response <- Ok(saved.toString)
      } yield response

      for {
        result <- write.attempt
        response <- result match {
          case Right(r) => IO.pure(r)
          case Left(e) => BadRequest(e.getMessage)
        }
      } yield response
  }
}
