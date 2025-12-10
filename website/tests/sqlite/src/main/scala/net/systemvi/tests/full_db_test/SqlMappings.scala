package net.systemvi.tests.full_db_test

import cats.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*

import java.util.UUID

object SqlMappings {
  given Read[UUID] = Read[String].map(UUID.fromString)
  given Write[UUID] = Write[String].contramap(_.toString)

  given Read[Keyboard] = Read[(UUID,String,UUID)].map(Keyboard.apply)
  given Write[Keyboard] = Write[(UUID,String,UUID)].contramap{k=>(k.uuid,k.name,k.switchId)}
  
  given Read[Manufacturer] = Read[(UUID,String)].map(Manufacturer.apply)
  given Write[Manufacturer] = Write[(UUID,String)].contramap{k=>(k.uuid,k.name)}
  
  given Read[Switch] = Read[(UUID,String,UUID,Int)].map{
    case (uuid,name,manufacturerId,switchTypeId)=>Switch(
      uuid,name,manufacturerId,switchTypeId match {
        case 0 => Linear
        case 1 => Tactile
        case 2 => Clicky
      }
    )
  }
  given Write[Switch] = Write[(UUID,String,UUID,Int)].contramap{k=>(k.uuid,k.name,k.manufacturerId,k.switchType.value)}
}
