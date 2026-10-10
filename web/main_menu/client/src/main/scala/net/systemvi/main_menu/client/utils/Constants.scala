package net.systemvi.main_menu.client.utils

import scala.scalajs.LinkingInfo.*

object Constants {

  val serverUrl: String = if developmentMode then "http://localhost:8080/api" else "/api"

  val clientUrl: String = if developmentMode then "http://localhost:5174" else ""
}
