package net.systemvi.website.routes

import java.util.UUID


object Pages {
  sealed abstract class Page(val title: String)

  case object HomePage extends Page("Home")

  case object KeyboardsPage extends Page("Keyboards")

  case object GamesPage extends Page("Games")

  case object EnginePage extends Page("Engine")

  case object ThreeDPrintingPage extends Page("3D Printing")

  case object KnittingPage extends Page("Knitting")

  case object OrigamiPage extends Page("Origami")

  case class KeyboardPage(keyboardId: UUID) extends Page("Keyboard")

  case class ApplicationDetailsPage(applicationUUID: UUID) extends Page("Application Details")

  case object ConfiguratorPage extends Page("Keyboard Configurator")

  case object NotFoundPage extends Page("404")
}