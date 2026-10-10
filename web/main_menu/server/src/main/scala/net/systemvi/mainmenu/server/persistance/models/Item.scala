package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

case class Item(
  uuid: UUID,
  name: String,
  imageUrl: String,
  description: Option[String],
  price: BigDecimal,
  categoryId: Option[UUID],
)
