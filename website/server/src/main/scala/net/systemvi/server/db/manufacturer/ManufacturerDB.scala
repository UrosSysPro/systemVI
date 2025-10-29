package net.systemvi.server.db.manufacturer

import net.systemvi.common.model.Manufacturer
import cats.effect.*
import cats.effect.implicits.*

object ManufacturerDB {

  def get(id:Int):IO[Option[Manufacturer]] = getAll().map(list=>list.find(m=>m.id==id))

  def getAll():IO[List[Manufacturer]] = IO.pure{
    List(
      Manufacturer(1,"Gateron"),
      Manufacturer(2,"Kailh"),
      Manufacturer(3,"Dark Project"),
    )
  }
}
