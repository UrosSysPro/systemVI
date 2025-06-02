package net.systemvi.website

import cats.*
import cats.implicits.*
import com.raquo.waypoint.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import org.scalajs.dom

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
  getPageTitle = page => page.title,
  serializePage = (page:Page)=>page.asJson.noSpaces,
  deserializePage = pageData=> (for{
    json<-parse(pageData)
    cursor=json.hcursor
    title<-cursor.get[String]("title")
  }yield title match{
    case "home" => HomePage
    case "keyboards" => KeyboardsPage
    case "games" => GamesPage
    case "engine" => EnginePage
    case "keyboard"=> json.as[KeyboardPage].getOrElse(HomePage)
  }).getOrElse(HomePage)

)