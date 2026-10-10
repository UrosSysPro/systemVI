package net.systemvi.mainmenu.common.dto

import io.circe.*
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.UUID

/**
 * The signed-in user as exposed to clients and carried inside the session JWT.
 *
 * `role` and `status` are the enum names, so clients do not need to know the
 * database ids.
 */
case class UserDto(
  uuid: UUID,
  role: String,
  status: String,
  name: Option[String],
  email: Option[String],
)

object UserDto {
  given encoder: Encoder[UserDto] = deriveEncoder
  given decoder: Decoder[UserDto] = deriveDecoder
}
