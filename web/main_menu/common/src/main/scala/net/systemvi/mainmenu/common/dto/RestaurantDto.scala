package net.systemvi.mainmenu.common.dto

import io.circe.*
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.UUID

case class RestaurantDto(
  uuid: UUID,
  name: String,
  description: Option[String],
  address: String,
  imageUrl: String,
)

object RestaurantDto {
  given encoder: Encoder[RestaurantDto] = deriveEncoder
  given decoder: Decoder[RestaurantDto] = deriveDecoder
}
