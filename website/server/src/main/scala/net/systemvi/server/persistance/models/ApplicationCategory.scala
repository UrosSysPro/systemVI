package net.systemvi.server.persistance.models

case class ApplicationCategory(
                                id: Int,
                                name: String
                              ) 

object Tool     extends ApplicationCategory(1,"Application")
object TechDemo extends ApplicationCategory(2,"Tech Demo")
object Game     extends ApplicationCategory(3,"Game")


object ApplicationCategory {
  val values: List[ApplicationCategory] = List(
    Tool,
    TechDemo,
    Game,
  )
}

