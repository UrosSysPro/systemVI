package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.website.views.*
import org.scalajs.dom
import scala.scalajs.js

val splitter= SplitRender[Page,HtmlElement](router.currentPageSignal)
  .collect[HomePage.type]{_ => HomePageView()}
  .collect[KeyboardsPage.type]{_ => KeyboardsPageView()}
  .collect[GamesPage.type ]{_ => GamesPageView()}
  .collect[EnginePage.type ]{_ => EnginePageView()}
  .collect[KeyboardPage]{page => KeyboardPageView(page)}
  .collect[GamePage] {page => GamePageView(page)}
  .collect[NotFoundPage.type ]{_=>div("page not found")}
  .collect[ConfiguratorPage.type ]{_=>ConfiguratorPageView()}


val app: Div = div(
    child <-- splitter.signal
)

@main
def main(): Unit = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    app
  )
}