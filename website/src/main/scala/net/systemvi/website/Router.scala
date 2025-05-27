package net.systemvi.website

import com.raquo.waypoint._
import org.scalajs.dom


sealed abstract class Page(val title: String)
case object HomePage extends Page("Home")
case object KeyboardsPage extends Page("Keyboards")
case object GamesPage extends Page("Games")
case object EnginePage extends Page("Engine")


val homeRoute = Route.static(HomePage, root / endOfSegments)
val keyboardsRoute = Route.static(KeyboardsPage, root / "keyboards" / endOfSegments)
val gamesRoute = Route.static(GamesPage, root / "games" / endOfSegments)
val engineRoute = Route.static(EnginePage, root / "engine" / endOfSegments)

object router extends Router[Page](
  routes = List(homeRoute,keyboardsRoute,gamesRoute,engineRoute),
  getPageTitle = page => page.title, // (document title, displayed in the browser history, and in the tab next to favicon)
  serializePage = page =>page.title, // serialize page data for storage in History API log
  deserializePage = pageData=>pageData.toLowerCase match {
    case "home" => HomePage
    case "keyboards" => KeyboardsPage
    case "games" => GamesPage
    case "engine" => EnginePage
  }// deserialize the above
)