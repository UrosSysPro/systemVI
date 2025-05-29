package net.systemvi.website

import com.raquo.waypoint._
//import org.scalajs.dom
//import io.circe.{*,given}
//import io.circe.generic.auto.{*,given}
//import io.circe.parser.{*,given}
//import io.circe.syntax.{*,given}

sealed abstract class Page(val title: String)
case object HomePage extends Page("Home")
case object KeyboardsPage extends Page("Keyboards")
case object GamesPage extends Page("Games")
case object EnginePage extends Page("Engine")

case class KeyboardPage(keyboardId:Int)extends Page("Keyboard")


val homeRoute = Route.static(HomePage, root / endOfSegments)
val keyboardsRoute = Route.static(KeyboardsPage, root / "keyboards" / endOfSegments)
val gamesRoute = Route.static(GamesPage, root / "games" / endOfSegments)
val engineRoute = Route.static(EnginePage, root / "engine" / endOfSegments)

val keyboardRoute=Route(
  encode = (page:KeyboardPage)=>page.keyboardId,
  decode = (args:Int)=>KeyboardPage(args),
  pattern = root / "keyboard" / segment[Int] / endOfSegments
)

object router extends Router[Page](
  routes = List(homeRoute,keyboardsRoute,gamesRoute,engineRoute,keyboardRoute),
  getPageTitle = page => page.title, // (document title, displayed in the browser history, and in the tab next to favicon)
  //serializePage = (page:Page)=>page.toJson, // serialize page data for storage in History API log
  serializePage = (page:Page)=>page.title, // serialize page data for storage in History API log
  deserializePage = pageData=>pageData.toLowerCase match {
    case "home" => HomePage
    case "keyboards" => KeyboardsPage
    case "games" => GamesPage
    case "engine" => EnginePage
  }// deserialize the above
)