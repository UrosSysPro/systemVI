package net.systemvi.mainmenu.common.dto

import java.util.UUID

case class MenuDto(
  uuid: UUID,
  name: String,
  description: Option[String],
)
