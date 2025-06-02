package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.api.{EngineApi, GameApi, KeyboardApi}
import net.systemvi.website.darkproject.footer.Footer
import net.systemvi.website.darkproject.navbar.Navbar
import net.systemvi.website.darkproject.section.{AboutSection, Section, SectionItem}
import net.systemvi.website.darkproject.slider.ImageSlider
import net.systemvi.website.*
import org.scalajs.dom

def HomePageView():Element = {
  val keyboards=KeyboardApi.all()
  val engine=EngineApi.get()
  val games=GameApi.all()
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      ImageSlider(
        images = List(
          "images/keyboards-all.jpg",
          "images/keyboards-all-rotated.jpg",
        )
      ),
      Section(
        "Keyboards",
        keyboards.take(4).map(k=>SectionItem(k.name,k.images.head,KeyboardPage(k.id))),
        KeyboardsPage
      ),
      Section(
        "Games",
        games.take(4).map(g=>SectionItem(g.name,g.images.head,HomePage)),
        GamesPage
      ),
      Section(
        "Engine Demo",
        engine.demos.take(4).map(e=>SectionItem(e.name,e.images.head,HomePage)),
        EnginePage
      ),
      AboutSection(),
      Footer(),
    )
  )
}