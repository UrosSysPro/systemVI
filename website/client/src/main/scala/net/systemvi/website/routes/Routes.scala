package net.systemvi.website.routes

import cats.*
import cats.implicits.*
import com.raquo.waypoint.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import net.systemvi.website.*
import org.scalajs.dom
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}
import urldsl.vocabulary.Segment.simpleSegment
import java.util.UUID
import scala.util.*
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.routes.PageDecoders.given

object Routes {
  val homeRoute: Route.Total[HomePage.type, Unit] = Route.static(HomePage, root / endOfSegments)
  val keyboardsRoute: Route.Total[KeyboardsPage.type, Unit] = Route.static(KeyboardsPage, root / "keyboards" / endOfSegments)
  val gamesRoute: Route.Total[GamesPage.type, Unit] = Route.static(GamesPage, root / "games" / endOfSegments)
  val engineRoute: Route.Total[EnginePage.type, Unit] = Route.static(EnginePage, root / "engine" / endOfSegments)
  val configuratorRoute: Route.Total[ConfiguratorPage.type, Unit] = Route.static(ConfiguratorPage, root / "configurator" / endOfSegments)
  val threeDPrintingRoute: Route.Total[ThreeDPrintingPage.type, Unit] = Route.static(ThreeDPrintingPage, root / "3dprinting" / endOfSegments)
  val knittingRoute: Route.Total[KnittingPage.type, Unit] = Route.static(KnittingPage, root / "knitting" / endOfSegments)
  val origamiRoute: Route.Total[OrigamiPage.type, Unit] = Route.static(OrigamiPage, root / "origami" / endOfSegments)

  val notFoundRoute: Route.Total[NotFoundPage.type, Unit] = Route.static(NotFoundPage, root)

  val keyboardRoute: Route.Total[KeyboardPage, UUID] = Route[KeyboardPage, UUID](
    encode = (page: KeyboardPage) => page.keyboardId,
    decode = (args: UUID) => KeyboardPage(args),
    pattern = root / "keyboard" / segment[UUID] / endOfSegments
  )

  val applicationDetailsRoute: Route.Total[ApplicationDetailsPage, UUID] = Route[ApplicationDetailsPage, UUID](
    encode = (page: ApplicationDetailsPage) => page.applicationUUID,
    decode = (appId: UUID) => ApplicationDetailsPage(appId),
    pattern = root / "application-details" / segment[UUID] / endOfSegments
  )

  val gameRoute: Route.Total[GamePage, Int] = Route[GamePage, Int](
    encode = (page: GamePage) => page.gameId,
    decode = (args: Int) => GamePage(args),
    pattern = root / "game" / segment[Int] / endOfSegments
  )
}
