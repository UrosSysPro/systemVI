package net.systemvi.website.client.views.engine

import com.raquo.laminar.api.L.{*, given}
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import net.systemvi.website.client.utils.Constants
import net.systemvi.website.client.views.applications.ApplicationsPageView
import net.systemvi.website.common.dtos.ApplicationDto
import org.scalajs.dom

import scala.concurrent.ExecutionContext

def EnginePageView():HtmlElement = {
  given ExecutionContext = ExecutionContext.global

  val engineDemosVar = EventStream.fromFuture(
    dom.fetch(s"${Constants.serverUrl}/applications/tech-demos")
      .toFuture
      .flatMap(_.json().toFuture)
      .map(decodeJs[List[ApplicationDto]](_))
      .map(_.getOrElse(List.empty))
  ).startWith(List.empty)

  ApplicationsPageView(
    title = "Engine",
    appsSignal = engineDemosVar.signal,
    showImageSlider = true
  )
}