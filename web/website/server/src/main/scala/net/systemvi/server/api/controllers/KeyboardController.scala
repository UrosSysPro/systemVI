package net.systemvi.server.api.controllers

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.persistance.models.*
import net.systemvi.common.dtos.*
import net.systemvi.server.services.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*
import java.util.UUID
import scala.util.*

private def getKeyboardDto(uuid: UUID)(using context: AppContext[IO]): IO[KeyboardDto] =
  for{
    keyboard <- context.db.keyboards.get(uuid).map(_.getOrElse(throw Exception("keyboard not found")))
    switchOption <- context.db.switches.get(keyboard.switchUUID)
    switch = switchOption.get

    switchManufacturerOption <- context.db.manufacturers.get(switch.manufacturerUUID)
    switchManufacturer = switchManufacturerOption.get

    images <- context.db.entityImages.get(keyboard.uuid)
    imageDtos = images.map(a=>EntityImageDto(a.imageUrl,a.order))

    specs <- context.db.entitySpecifications.get(keyboard.uuid)
    specDtos = specs.map(a=>EntitySpecificationDto(a.key,a.value,a.order))

    profile = KeyboardProfile.values.find(_.id == keyboard.profileId).get

    filament <- context.db.filaments.get(keyboard.filamentUUID).map(_.get)
    filamentManufacturer <- context.db.manufacturers.get(filament.manufacturerUUID).map(_.get)
    polymer = FilamentPolymer.values.find(_.id == filament.polymerId).get

    switchType = SwitchType.values.find(_.id == switch.switchTypeId).get
  }yield KeyboardDto(
    uuid = keyboard.uuid,
    name = keyboard.name,
    codeName = keyboard.codeName,
    profile = KeyboardProfileDto(
      id = profile.id,
      name = profile.name,
    ),
    switch = SwitchDto(
      uuid = switch.uuid,
      name = switch.name,
      switchType = SwitchTypeDto(
        id = switchType.id,
        name = switchType.name,
      ),
      manufacturer = ManufacturerDto(
        uuid = switchManufacturer.uuid,
        name = switchManufacturer.name,
      ),
    ),
    filament = FilamentDto(
      polymer = FilamentPolymerDto(
        name = polymer.name
      ),
      manufacturer = ManufacturerDto(
        uuid = filamentManufacturer.uuid,
        name = filamentManufacturer.name,
      ),
      name = filament.name,
    ),
    images = imageDtos,
    specs = specDtos,
  )


def keyboardController(using context: AppContext[IO]) = HttpRoutes.of[IO]{
  case GET -> Root => for{
    keyboards <- context.db.keyboards.get()
    dtos <- keyboards.map(_.uuid).traverse(getKeyboardDto)
    response <- Ok(dtos.asJson)
  } yield response

  case GET -> Root / UUIDVar(uuid) => for{
    dto <- getKeyboardDto(uuid)
    response <- Ok(dto.asJson)
  } yield response
}
