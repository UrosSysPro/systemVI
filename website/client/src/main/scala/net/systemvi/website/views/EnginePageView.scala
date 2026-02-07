package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.common.dtos.ApplicationDto
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import net.systemvi.website.utils.Constants
import org.scalajs.dom
def EnginePageView():HtmlElement = {
  val engineDemosVar = EventStream.fromFuture(
    dom.fetch(s"${Constants.serverUrl}/applications")
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