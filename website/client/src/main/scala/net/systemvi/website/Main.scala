package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.routes.Router
import net.systemvi.website.views.*
import net.systemvi.website.views.details.*
import org.scalajs.dom

val splitter = SplitRender[Page, HtmlElement](Router.currentPageSignal)
  .collect[HomePage.type]           { _ => HomePageView()}
  .collect[KeyboardsPage.type]      { _ => KeyboardsPageView()}
  .collect[GamesPage.type]          { _ => GamesPageView()}
  .collect[EnginePage.type]         { _ => EnginePageView()}
  .collect[KeyboardPage]            { page => KeyboardPageView(page)}
  .collect[GamePage]                { page => GamePageView(page)}
  .collect[ConfiguratorPage.type]   { _ => ConfiguratorPageView()}
  .collect[ThreeDPrintingPage.type] { _ => div("coming soon") }
  .collect[KnittingPage.type]       { _ => div("coming soon") }
  .collect[OrigamiPage.type]        { _ => div("coming soon") }
  .collect[NotFoundPage.type]       { _ => div("page not found") }

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