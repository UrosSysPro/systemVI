package net.systemvi.common.model

case class Game(id:Int,name:String,codeName:String,specs:List[ProductSpec]=List(),images:List[String])
