package net.systemvi.website.model

case class KeyboardSpec(name:String, value:String)
case class Keyboard(id:Int,name:String, codeName:String, specs:List[KeyboardSpec], images:List[String])

case class Game()

case class Engine()