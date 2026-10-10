package net.systemvi.mainmenu.common.dto

import java.util.UUID

case class ItemDto(
  uuid: UUID,
  name: String,
  imageUrl: String,
  description: Option[String],
  price: BigDecimal,
  categoryId: Option[UUID],
)
