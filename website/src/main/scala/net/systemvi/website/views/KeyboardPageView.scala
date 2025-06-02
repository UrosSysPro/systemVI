package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.KeyboardPage
import net.systemvi.website.api.KeyboardApi
import net.systemvi.website.footer.Footer
import net.systemvi.website.navbar.Navbar
import net.systemvi.website.section.{Section, SectionItem}
import net.systemvi.website.slider.ImageSlider
import net.systemvi.website.keyboard_info.KeyboardInfo
import net.systemvi.website.bento_box.{BentoBoxItem, *}
import org.scalajs.dom
import net.systemvi.website.model.*
import net.systemvi.website.big_title.BigTitle
import net.systemvi.website.expandable_specs.ExpandableSpecs
import net.systemvi.website.bill_of_materials.BillOfMaterials

def KeyboardPageView(page:KeyboardPage):HtmlElement = {
  val keyboard=KeyboardApi.get(page.keyboardId)
  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      KeyboardInfo(keyboard),
      ImageSlider(keyboard.images),
      BigTitle(
        keyboard.name,
        """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          | Phasellus aliquet purus a fringilla condimentum. Praesent
          | vestibulum enim neque, eu placerat nulla molestie in
          | """.stripMargin
      ),
      //4x3
      BentoBox(
        BentoBoxSize(4,3),
        List(
          BentoBoxItem("area_1",BentoBoxRect(0,0,2,1),div(width.percent:=100,height.percent:=100,backgroundColor.red)),
          BentoBoxItem("area_2",BentoBoxRect(2,0,1,2),div(width.percent:=100,height.percent:=100,backgroundColor.green)),
          BentoBoxItem("area_3",BentoBoxRect(3,0,1,1),div(width.percent:=100,height.percent:=100,backgroundColor.blue)),
          BentoBoxItem("area_4",BentoBoxRect(0,1,1,1),div(width.percent:=100,height.percent:=100,backgroundColor.fuchsia)),
          BentoBoxItem("area_5",BentoBoxRect(1,1,1,1),div(width.percent:=100,height.percent:=100,backgroundColor.cyan)),
          BentoBoxItem("area_6",BentoBoxRect(3,1,1,1),div(width.percent:=100,height.percent:=100,backgroundColor.gray)),
          BentoBoxItem("area_7",BentoBoxRect(0,2,2,1),div(width.percent:=100,height.percent:=100,backgroundColor.rgb(246,246,246))),
          BentoBoxItem("area_8",BentoBoxRect(2,2,2,1),div(width.percent:=100,height.percent:=100,backgroundColor.rgb(51,51,51))),
        ),
        width.percent:=100,
        height.rem:=50,
        padding.rem:=1,
      ),
      BigTitle("Technical Specifications"),
      ExpandableSpecs(keyboard),
      BigTitle("Bill Of Materials"),
      BillOfMaterials(),
      Footer(),
    )
  )
}