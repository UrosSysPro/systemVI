package net.systemvi.tests.full_db_test

import java.util.UUID

case class Manufacturer(uuid: UUID,name:String)

sealed trait SwitchType(value:Int)
object Linear extends SwitchType(0)
object Tactile extends SwitchType(1)
object Clicky extends SwitchType(2)

case class Switch(uuid:UUID, name:String, manufacturerId:UUID, switchType: SwitchType)

case class Keyboard(uuid:UUID, name:String, switchId:UUID)
