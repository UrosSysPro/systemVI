package net.systemvi.website.client.views.applications

import com.raquo.laminar.api.L.{*, given}
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.client.*
import net.systemvi.website.client.utils.Constants
import net.systemvi.website.common.dtos.ApplicationDto
import org.scalajs.dom

import scala.concurrent.ExecutionContext


def GamesPageView(): HtmlElement = {
  given ExecutionContext = ExecutionContext.global

  val gamesVar = EventStream.fromFuture(
    dom.fetch(s"${Constants.serverUrl}/applications/games")
      .toFuture
      .flatMap(_.json().toFuture)
      .map(decodeJs[List[ApplicationDto]](_))
      .map(_.getOrElse(List.empty))
  ).startWith(List.empty)

  ApplicationsPageView(
    title = "Games",
    appsSignal = gamesVar.signal,
    showImageSlider = false
  )
}