package net.systemvi.server.persistance.mappings

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.{Manufacturer, Switch}

import java.util.UUID

object SqlMappings {
  given Read[UUID] = Read[String].map(UUID.fromString)
  given Write[UUID] = Write[String].contramap(_.toString)

  given Read[Manufacturer] = Read[(UUID,String)].map(Manufacturer.apply)

  given Read[Switch] = Read[(UUID,UUID,Int,String)].map(Switch.apply)

}
