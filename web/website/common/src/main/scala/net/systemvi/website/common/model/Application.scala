package net.systemvi.website.common.model

case class Application(
                        id:Int,
                        version:String,
                        name:String,
                        codeName:String,
                        description:String,
                        screenshots:List[String],
                        downloadLinks:List[DownloadLink]
                      )
