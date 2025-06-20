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

given Decoder[HomePage.type] = cursor=>for{
  _<-cursor.get[Json]("HomePage")
}yield HomePage
given Decoder[GamesPage.type] = cursor => for{
  _<-cursor.get[Json]("GamesPage")
}yield GamesPage
given Decoder[KeyboardsPage.type] =cursor=>for{
  _<-cursor.get[Json]("KeyboardsPage")
}yield KeyboardsPage
given Decoder[EnginePage.type] = cursor=>for{
  _<-cursor.get[Json]("EnginePage")
}yield EnginePage
given Decoder[NotFoundPage.type] =cursor=>for{
  _<-cursor.get[Json]("NotFoundPage")
}yield NotFoundPage
given Decoder[GamePage] = cursor=>for{
  child<-cursor.get[Json]("GamePage")
  id<-child.hcursor.get[Int]("gameId")
}yield GamePage(id)
given Decoder[KeyboardPage] = cursor=>for{
  child<-cursor.get[Json]("KeyboardPage")
  id<-child.hcursor.get[Int]("keyboardId")
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
    ).flatMap{
        case Right(page)=>List(page)
        case Left(_)=>List.empty
      }
      .get(0)
      .getOrElse(NotFoundPage)
    page
  }
)