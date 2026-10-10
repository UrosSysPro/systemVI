package net.systemvi.website.common.model

case class DownloadLink(
                         name:String,
                         url:String,
                         version:String,
                         platform:String,
                         description:String
                       )