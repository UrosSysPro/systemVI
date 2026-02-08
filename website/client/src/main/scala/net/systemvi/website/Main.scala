package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.routes.Router
import net.systemvi.website.views.*
import net.systemvi.website.views.details.*
import org.scalajs.dom

val splitter = SplitRender[Page, HtmlElement](Router.currentPageSignal)
  .collectStatic(HomePage)          { HomePageView() }
  .collectStatic(KeyboardsPage)     { KeyboardsPageView() }
  .collectStatic(GamesPage)         { GamesPageView() }
  .collectStatic(EnginePage)        { EnginePageView() }
  .collect[KeyboardPage]            { page => KeyboardPageView(page) }
  .collect[ApplicationDetailsPage]  { page => ApplicationDetailsPageView(page) }
  .collectStatic(ConfiguratorPage)  { ConfiguratorPageView() }
  .collectStatic(ThreeDPrintingPage){ div("coming soon") }
  .collectStatic(KnittingPage)      { div("coming soon") }
  .collectStatic(OrigamiPage)       { div("coming soon") }
  .collectStatic(NotFoundPage)      { div("page not found") }

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