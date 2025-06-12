package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.views.*
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.*


def renderPage(page: Page): Element = {

  page match {
    case HomePage => HomePageView()
    case KeyboardsPage => KeyboardsPageView()
    case GamesPage => GamesPageView()
    case EnginePage => EnginePageView()
    case page:KeyboardPage=> KeyboardPageView(page)
    case page:GamePage=> GamePageView(page)
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