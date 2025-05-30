package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.KeyboardPage
import net.systemvi.website.api.KeyboardApi
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider
import net.systemvi.website.keyboard_info.KeyboardInfo
import org.scalajs.dom
import net.systemvi.website.model.*


def KeyboardPageView(page:KeyboardPage):Element = {
  val keyboard=KeyboardApi.get(page.keyboardId)
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      KeyboardInfo(keyboard),
      ImageSlider(keyboard.images),
      Footer(),
    )
  )
}