package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.{GamePage, HomePage}
import net.systemvi.website.api.GameApi
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.footer.Footer
import net.systemvi.website.darkproject.neo_navbar.NeoNavbar
import net.systemvi.website.darkproject.section.{Section, SectionItem}
import net.systemvi.website.darkproject.slider.ImageSlider
import org.scalajs.dom

def GamesPageView():HtmlElement = {
  val games=GameApi.all()
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      NeoNavbar(),
      BigTitle("Games"),
      Section(
        title="",
        items = games.map(g=>SectionItem(g.name,g.images.head,GamePage(g.id)))
      ),
      Footer(),
    )
  )
}