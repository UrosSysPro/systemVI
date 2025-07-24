package net.systemvi.common.model

case class DownloadLink(name:String,url:String, version:String, platform:String, description:String)

case class ProductSpec(name:String, value:String)
case class Keyboard(id:Int, name:String, codeName:String, specs:List[ProductSpec], images:List[String])

case class Game(id:Int,name:String,codeName:String,specs:List[ProductSpec]=List(),images:List[String])

case class Engine(id:Int,version:String,name:String,codeName:String,description:String,demos:List[EngineDemo])
case class EngineDemo(id:Int,name:String,description:String,images:List[String])

case class Application(id:Int,version:String,name:String,codeName:String,description:String,screenshots:List[String],downloadLinks:List[DownloadLink])