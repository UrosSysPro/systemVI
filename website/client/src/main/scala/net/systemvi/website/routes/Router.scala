package net.systemvi.website.routes

import cats.*
import cats.implicits.*
import com.raquo.waypoint.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import net.systemvi.website.*
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.routes.Routes.*
import java.util.UUID
import scala.util.*
import org.scalajs.dom

object Router extends Router[Page](
  routes = List(
    homeRoute,
    keyboardsRoute,
    gamesRoute,
    engineRoute,
    keyboardRoute,
    applicationDetailsRoute,
    gameRoute,
    configuratorRoute,
    threeDPrintingRoute,
    knittingRoute,
    origamiRoute,
    notFoundRoute,
  ),
  getPageTitle = page => page.title,
  serializePage = (page: Page) => {
//    dom.console.log("serialize: ", page.asJson.noSpaces)
    page.asJson.noSpaces
  },
  deserializePage = pageData => {
//    dom.console.log("deserialize: ", pageData)
    val json = parse(pageData).getOrElse(Json.obj())
    val page = List(
      json.as[HomePage.type],
      json.as[KeyboardsPage.type],
      json.as[GamesPage.type],
      json.as[EnginePage.type],
      json.as[NotFoundPage.type],
      json.as[KeyboardPage],
      json.as[ApplicationDetailsPage],
      json.as[GamePage],
      json.as[ConfiguratorPage.type],
      json.as[ThreeDPrintingPage.type],
      json.as[KnittingPage.type],
      json.as[OrigamiPage.type]
    ).flatMap{
        case Right(page) => List(page)
        case Left(_) => List.empty
      }
      .get(0)
      .getOrElse(NotFoundPage)
    page
  }
)