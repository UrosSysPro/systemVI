package net.systemvi.common.dtos


import java.util.UUID

case class UserDto (
                  uuid: UUID,
                  email: String,
                  name: String,
                  picture: Option[String],
                )
