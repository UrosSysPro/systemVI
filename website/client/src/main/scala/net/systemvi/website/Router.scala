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

case class KeyboardPage(keyboardId: Int)extends Page("Keyboard")
case class GamePage(gameId: Int) extends Page("Game")
case object NotFoundPage extends Page("404")

//encoders
given Encoder[HomePage.type] = page=>Json.obj(
  "home-page"->Json.fromString(page.title)
)
given Encoder[KeyboardsPage.type] = page=>Json.obj(
  "keyboards-page"->Json.fromString(page.title)
)
given Encoder[GamesPage.type] = page=>Json.obj(
  "games-page"->Json.fromString(page.title)
)
given Encoder[EnginePage.type] = page => Json.obj(
  "engine-page" -> Json.fromString(page.title)
)
given Encoder[NotFoundPage.type] = page => Json.obj(
  "not-found-page" -> Json.fromString(page.title)
)
given Encoder[KeyboardPage] = page => Json.obj(
  "keyboard-page" -> Json.fromString(page.title),
  "id"->Json.fromInt(page.keyboardId)
)
given Encoder[GamePage] = page => Json.obj(
  "game-page" -> Json.fromString(page.title),
  "id"->Json.fromInt(page.gameId)
)
//decoders
given Decoder[HomePage.type] = cursor=>for{
  _<-cursor.downField("HomePage").get[String]("home-page")
}yield HomePage
given Decoder[GamesPage.type] = cursor => for{
  _<-cursor.downField("GamesPage").get[String]("games-page")
}yield GamesPage
given Decoder[KeyboardsPage.type] =cursor=>for{
  _<-cursor.downField("KeyboardsPage").get[String]("keyboards-page")
}yield KeyboardsPage
given Decoder[EnginePage.type] = cursor=>for{
  _<-cursor.downField("EnginePage").get[String]("engine-page")
}yield EnginePage
given Decoder[NotFoundPage.type] =cursor=>for{
  _<-cursor.downField("NotFoundPage").get[String]("not-found-page")
}yield NotFoundPage
given Decoder[GamePage] = cursor=>for{
  _<-cursor.downField("GamePage").get[String]("game-page")
  id<-cursor.downField("GamePage").get[Int]("id")
}yield GamePage(id)
given Decoder[KeyboardPage] = cursor=>for{
  _<-cursor.downField("KeyboardPage").get[String]("keyboard-page")
  id<-cursor.downField("KeyboardPage").get[Int]("id")
}yield KeyboardPage(id)

val homeRoute = Route.static(HomePage, root / endOfSegments)
val keyboardsRoute = Route.static(KeyboardsPage, root / "keyboards" / endOfSegments)
val gamesRoute = Route.static(GamesPage, root / "games" / endOfSegments)
val engineRoute = Route.static(EnginePage, root / "engine" / endOfSegments)

val notFoundRoute=Route.static(NotFoundPage,root)

val keyboardRoute=Route[KeyboardPage,Int](
  encode = (page:KeyboardPage)=>page.keyboardId,
  decode = (args:Int)=>KeyboardPage(args),
  pattern = root / "keyboard" / segment[Int] / endOfSegments
)

val gameRoute=Route[GamePage,Int](
  encode = (page:GamePage)=>page.gameId,
  decode = (args:Int)=>GamePage(args),
  pattern = root / "game" / segment[Int] / endOfSegments
)

object router extends Router[Page](
  routes = List(
    homeRoute,
    keyboardsRoute,
    gamesRoute,
    engineRoute,
    keyboardRoute,
    gameRoute,
    notFoundRoute,
  ),
  getPageTitle = page => page.title,
  serializePage = (page:Page)=>{
    page.asJson.noSpaces
  },
  deserializePage = pageData=>{
    val json=parse(pageData).getOrElse(Json.obj())
    val page=List(
      json.as[HomePage.type],
      json.as[KeyboardsPage.type],
      json.as[GamesPage.type],
      json.as[EnginePage.type],
      json.as[NotFoundPage.type],
      json.as[KeyboardPage],
      json.as[GamePage],
    )
//      .map{ e =>
//        dom.console.log(e)
//        e
//      }
      .map{
        case Right(page) => Some(page)
        case _ => None
      }
//      .map{ e =>
//        dom.console.log(e.asJson.toString)
//        e
//      }
      .filter{
        case Some(_)=>true
        case None=>false
      }
//      .map{ e =>
//        dom.console.log(e.asJson.toString)
//        e
//      }
      .map{
        _.getOrElse(NotFoundPage)
      }
      .get(0)
      .getOrElse(NotFoundPage)
    page
  }
)