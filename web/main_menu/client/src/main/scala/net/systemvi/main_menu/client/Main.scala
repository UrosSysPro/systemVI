package net.systemvi.main_menu.client

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.SplitRender
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router
import net.systemvi.main_menu.client.views.{CategoryPageView, ItemsPageView}
import org.scalajs.dom

val splitter = SplitRender[Page, HtmlElement](Router.currentPageSignal)
  .collectStatic(CategoriesPage) { CategoryPageView() }
  .collectStatic(ItemsPage)      { ItemsPageView() }
  .collectStatic(NotFoundPage)   { div("page not found") }

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
