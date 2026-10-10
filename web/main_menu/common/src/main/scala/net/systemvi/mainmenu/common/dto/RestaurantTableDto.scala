package net.systemvi.mainmenu.common.dto

import io.circe.*
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.UUID

case class RestaurantTableDto(
  uuid: UUID,
  name: String,
  imageUrl: String,
  restaurantId: UUID,
)

object RestaurantTableDto {
  given encoder: Encoder[RestaurantTableDto] = deriveEncoder
  given decoder: Decoder[RestaurantTableDto] = deriveDecoder
}
