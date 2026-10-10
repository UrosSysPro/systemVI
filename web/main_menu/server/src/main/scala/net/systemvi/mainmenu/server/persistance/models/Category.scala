package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

case class Category(
  uuid: UUID,
  name: String,
  description: Option[String],
  imageUrl: String,
  parentId: Option[UUID],
  restaurantId: Option[UUID],
)
