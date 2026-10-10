package net.systemvi.mainmenu.server.persistance.mappings

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import net.systemvi.mainmenu.server.persistance.models.Item
import net.systemvi.mainmenu.server.persistance.models.Category
import net.systemvi.mainmenu.server.persistance.models.Restaurant
import net.systemvi.mainmenu.server.persistance.models.RestaurantTable

import java.util.UUID

object SqlMappings {
  given Read[UUID] = Read[String].map(UUID.fromString)
  given Write[UUID] = Write[String].contramap(_.toString)

  given Read[Item] = Read[(UUID, String, String, Option[String], BigDecimal, Option[UUID])].map(Item.apply)

  given Read[Category] = Read[(UUID, String, Option[String], String, Option[UUID], Option[UUID])].map(Category.apply)

  given Read[Restaurant] = Read[(UUID, String, Option[String], String, String)].map(Restaurant.apply)

  given Read[RestaurantTable] = Read[(UUID, String, String, UUID)].map(RestaurantTable.apply)
}
