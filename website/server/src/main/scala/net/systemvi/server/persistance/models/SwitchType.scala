package net.systemvi.server.persistance.models

sealed class SwitchType(val id:Int,val name: String)

object Clicky extends SwitchType(1,"Clicky")
object Tactile extends SwitchType(2,"Tactile")
object Linear extends SwitchType(3,"Linear")

object SwitchType {
  val values: List[SwitchType] = List(
    Clicky,
    Tactile,
    Linear,
  )
}