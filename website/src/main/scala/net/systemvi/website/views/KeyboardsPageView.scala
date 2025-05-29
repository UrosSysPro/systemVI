package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider
import org.scalajs.dom

def KeyboardsPageView():Element = {
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),

      Footer(),
    )
  )
}