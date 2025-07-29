package net.systemvi.common.model

case class DownloadLink(
                         name:String,
                         url:String,
                         version:String,
                         platform:String,
                         description:String
                       )