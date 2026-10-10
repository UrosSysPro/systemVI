package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

case class Menu(
  uuid: UUID,
  name: String,
  description: Option[String],
)
