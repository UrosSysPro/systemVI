package net.systemvi.server.api.controllers

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import net.systemvi.common.dtos.*
import net.systemvi.server.persistance.contexts.ApplicationContext
import net.systemvi.server.persistance.models.{Manufacturer, SwitchType}
import net.systemvi.server.services.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*

def switchController(using context: ApplicationContext[IO]) = HttpRoutes.of[IO]{
  case GET -> Root =>
    for{
      switches <- context.db.switches.get()
      data <- switches.map{switch =>
        for{
          manufacturerOption <- context.db.manufacturers.get(switch.manufacturerUUID)
          manufacturer = manufacturerOption.get
          switchType = SwitchType.values.find(_.id==switch.switchTypeId).get
        } yield SwitchDto(
          uuid = switch.uuid,
          name = switch.name,
          manufacturer = ManufacturerDto(
            uuid = manufacturer.uuid,
            name = manufacturer.name,
          ),
          switchType = SwitchTypeDto(
            id = switchType.id,
            name = switchType.name
          )
        )
      }.sequence
      response <- Ok(data.asJson)
    } yield response
  
  case GET -> Root / UUIDVar(id) =>
    for{
      switches <- context.db.switches.get(id)
      dto <- switches.map{switch =>
        for{
          manufacturerOption <- context.db.manufacturers.get(switch.manufacturerUUID)
          manufacturer = manufacturerOption.get
          switchType = SwitchType.values.find(_.id==switch.switchTypeId).get
        } yield SwitchDto(
          uuid = switch.uuid,
          name = switch.name,
          manufacturer = ManufacturerDto(
            uuid = manufacturer.uuid,
            name = manufacturer.name,
          ),
          switchType = SwitchTypeDto(
            id = switchType.id,
            name = switchType.name
          )
        )
      }.sequence
      response <- Ok(dto.asJson)
    } yield response

}
