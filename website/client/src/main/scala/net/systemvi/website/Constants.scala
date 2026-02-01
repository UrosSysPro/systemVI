package net.systemvi.website

import scala.scalajs.LinkingInfo.*

object Constants {

  val serverUrl: String = if developmentMode then "http://localhost:8080/api" else "https://systemvi.net/api"

  val clientUrl: String = if developmentMode then "http://localhost:5173" else "https://systemvi.net"
}
