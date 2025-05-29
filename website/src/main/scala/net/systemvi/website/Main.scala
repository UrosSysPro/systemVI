package net.systemvi.website

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.views.*
import org.scalajs.dom


def renderPage(page: Page): Element = {
  page match {
    case HomePage => HomePageView()
    case KeyboardsPage => KeyboardsPageView()
    case GamesPage => GamesPageView()
    case EnginePage => EnginePageView()
  }
}

val app: Div = div(
  child <-- router.currentPageSignal.map(renderPage)
)

@main
def main(): Unit = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    app
  )
}