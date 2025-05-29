package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.KeyboardPage
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider
import net.systemvi.website.product_info.{Product, ProductInfo, Spec}
import org.scalajs.dom


def KeyboardPageView(page:KeyboardPage):Element = {
  dom.console.log(page)
  val product=Product(
    name = "Product Name",
    codeName = "Code Name",
    specs = List(
      Spec(name = "spec 1", value = "value 1"),
      Spec(name = "spec 2", value = "value 2"),
      Spec(name = "spec 3", value = "value 3"),
      Spec(name = "spec 4", value = "value 4"),
    ),
    images = List(
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
      "/images/corne-wireless.jpg",
    )
  )
  val images=List(
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
    "/images/corne-wireless.jpg",
  )
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      ProductInfo(product),
      ImageSlider(images),
      Footer(),
    )
  )
}