package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

case class Restaurant(
  uuid: UUID,
  name: String,
  description: Option[String],
  address: String,
  imageUrl: String,
)
