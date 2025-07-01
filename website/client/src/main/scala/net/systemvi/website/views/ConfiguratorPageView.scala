package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.api.ApplicationApi
import org.scalajs.dom
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.navbar.Navbar
import net.systemvi.website.darkproject.product_info.{ProductInfo, given}
import net.systemvi.website.darkproject.expandable_specs.ExpandableSpecs
import net.systemvi.website.darkproject.bill_of_materials
import net.systemvi.website.darkproject.slider.ImageSlider
import net.systemvi.website.darkproject.footer.Footer

def ConfiguratorPageView():HtmlElement={
  val application=ApplicationApi.get(0)
  application.map{app=>
    div(
      cls := "flex flex-col items-center pt-24",
      div(
        className := "flex flex-col justify-start w-full max-w-[1450px]",
        Navbar(),
        BigTitle(app.name),
        ProductInfo(app),
        ImageSlider(app.screenshots),

        Footer(),
      )
    )
  }.getOrElse(div())
}
