package net.systemvi.server.persistance.models

sealed trait FilamentPolymer(
                              val id: Int,
                              val name: String
                            )
object Pla extends FilamentPolymer(1,"Pla")
object PlaPlus extends FilamentPolymer(2,"Pla+")

object FilamentPolymer {
  val values: List[FilamentPolymer] = List(
    Pla,
    PlaPlus,
  )
}