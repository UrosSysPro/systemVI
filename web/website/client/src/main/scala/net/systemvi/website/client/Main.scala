package net.systemvi.website.client

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.website.client.routes.*
import net.systemvi.website.client.routes.Pages.*
import net.systemvi.website.client.routes.Router
import net.systemvi.website.client.views.applications.{ApplicationDetailsPageView, ConfiguratorPageView, GamesPageView}
import net.systemvi.website.client.views.catan.CatanCardsPageView
import net.systemvi.website.client.views.engine.EnginePageView
import net.systemvi.website.client.views.fourier.{Hearth, discreteFourierSeriesView}
import net.systemvi.website.client.views.home.HomePageView
import net.systemvi.website.client.views.keyboard.{KeyboardPageView, KeyboardsPageView}
import net.systemvi.website.client.views.profile.UserProfilePageView
import net.systemvi.website.client.views.threeDPrinting.{ThreeDPrintedProductDetailsPageView, ThreeDPrintedProductsPageView, ThreeDPrintingPageView}
import org.scalajs.dom

val splitter = SplitRender[Page, HtmlElement](Router.currentPageSignal)
  .collectStatic(HomePage)                        { HomePageView() }

  .collectStatic(KeyboardsPage)                   { KeyboardsPageView() }
  .collectStatic(GamesPage)                       { GamesPageView() }
  .collectStatic(EnginePage)                      { EnginePageView() }
  .collect      [KeyboardPage]                    { page => KeyboardPageView(page) }
  .collect      [ApplicationDetailsPage]          { page => ApplicationDetailsPageView(page) }
  .collectStatic(ConfiguratorPage)                { ConfiguratorPageView() }

  .collectStatic(DiscreteFourierSeriesPage)       { discreteFourierSeriesView() }
  .collectStatic(HearthPage)                      { Hearth.discreteFourierSeriesView() }

  .collectStatic(ThreeDPrintingPage)              { ThreeDPrintingPageView.component() }
  .collectStatic(ThreeDPrintedProductsPage)       { ThreeDPrintedProductsPageView.component() }
  .collect      [ThreeDPrintedProductDetailsPage] { page => ThreeDPrintedProductDetailsPageView(page) }

  .collectStatic(KnittingPage)                    { div("coming soon") }
  .collectStatic(OrigamiPage)                     { div("coming soon") }

  .collectStatic(UserProfilePage)                 { UserProfilePageView() }

  .collectStatic(CatanCardsPage)                  { CatanCardsPageView }
  .collectStatic(NotFoundPage)                    { div("page not found") }

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
