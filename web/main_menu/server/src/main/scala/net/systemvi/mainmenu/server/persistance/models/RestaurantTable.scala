package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

/** A physical table where guests sit and order from. */
case class RestaurantTable(
  uuid: UUID,
  name: String,
  imageUrl: String,
  restaurantId: UUID,
)
