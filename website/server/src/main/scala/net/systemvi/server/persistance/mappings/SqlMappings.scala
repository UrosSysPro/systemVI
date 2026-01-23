package net.systemvi.server.persistance.mappings

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.*
import java.util.UUID

object SqlMappings {
  given Read[UUID] = Read[String].map(UUID.fromString)
  given Write[UUID] = Write[String].contramap(_.toString)

  given Read[Manufacturer] = Read[(UUID,String)].map(Manufacturer.apply)

  given Read[Switch] = Read[(UUID,UUID,Int,String)].map(Switch.apply)

  given Read[Keyboard] = Read[(UUID,UUID,Int,UUID,String,String)].map(Keyboard.apply)

  given Read[Application] = Read[(UUID,Int,String,String,String)].map(Application.apply)
  given Write[Application] = Write[(UUID,Int,String,String,String)].contramap(a=>(a.uuid,a.categoryId,a.name,a.codeName,a.description))

  given Read[EntityImage] = Read[(UUID,String,Int)].map(EntityImage.apply)
  
  given Read[EntitySpecification] = Read[(UUID,String,String,Int)].map(EntitySpecification.apply)
}
