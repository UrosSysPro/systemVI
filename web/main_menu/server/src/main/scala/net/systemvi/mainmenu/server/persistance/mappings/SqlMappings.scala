package net.systemvi.mainmenu.server.persistance.mappings

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import net.systemvi.mainmenu.server.persistance.models.Menu
import net.systemvi.mainmenu.server.persistance.models.Item
import net.systemvi.mainmenu.server.persistance.models.Category

import java.util.UUID

object SqlMappings {
  given Read[UUID] = Read[String].map(UUID.fromString)
  given Write[UUID] = Write[String].contramap(_.toString)

  given Read[Menu] = Read[(UUID, String, Option[String])].map(Menu.apply)

  given Read[Item] = Read[(UUID, String, String, Option[String], BigDecimal, Option[UUID])].map(Item.apply)

  given Read[Category] = Read[(UUID, String, Option[String], String, Option[UUID])].map(Category.apply)
}
