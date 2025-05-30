package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.HomePage
import net.systemvi.website.api.EngineApi
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider
import net.systemvi.website.big_title.BigTitle
import net.systemvi.website.model.*
import org.scalajs.dom

def EnginePageView():Element = {
  val engine=EngineApi.get()
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      BigTitle("Engine"),
      ImageSlider(engine.demos.flatMap(d=>d.images)),
      Section(
        title = "",
        items = engine.demos.map(d=>SectionItem(d.name,d.images.head,HomePage))
      ),
      Footer(),
    )
  )
}