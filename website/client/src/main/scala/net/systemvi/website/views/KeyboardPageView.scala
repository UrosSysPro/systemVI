package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import net.systemvi.website.KeyboardPage
import net.systemvi.website.api.KeyboardApi
import net.systemvi.website.darkproject.bento_box.{BentoBox, BentoBoxItem, BentoBoxRect, BentoBoxSize}
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.bill_of_materials.BillOfMaterials
import net.systemvi.website.darkproject.expandable_specs.ExpandableSpecs
import net.systemvi.website.darkproject.footer.Footer
import net.systemvi.website.darkproject.keyboard_info.KeyboardInfo
import net.systemvi.website.darkproject.navbar.Navbar
import net.systemvi.website.darkproject.slider.ImageSlider
import org.scalajs.dom

sealed trait ScreenSize(val width:Int)
object Small extends ScreenSize(500)
object Medium extends ScreenSize(1000)
object Large extends ScreenSize(1300)
object Extra extends ScreenSize(1500)

case class BentoBoxData(size:BentoBoxSize,items:List[BentoBoxRect])

def KeyboardPageView(page:KeyboardPage):HtmlElement = {
  val keyboard=KeyboardApi.get(page.keyboardId)

  def screenSizeToEnum=dom.window.innerWidth match{
    case x if x < Small.width => Small
    case x if x < Medium.width => Medium
    case x if x < Large.width => Large
    case _ => Extra
  }

  val screenSize=windowEvents(_.onResize).map(_=>screenSizeToEnum).distinct.startWith(screenSizeToEnum)

  val boxItems=List.range(0,8).map(_=>div(
    padding:="0.25rem",
    height.percent:=100,
    div(
      borderRadius.rem:=1,
      height.percent:=100,
      backgroundColor.rgb(246,246,246),
    )
  ))

  val boxes = screenSize.map{
    case Small =>BentoBoxData(
      BentoBoxSize(1,8),
      List(
        BentoBoxRect(0,0,1,1),
        BentoBoxRect(0,1,1,1),
        BentoBoxRect(0,2,1,1),
        BentoBoxRect(0,3,1,1),
        BentoBoxRect(0,4,1,1),
        BentoBoxRect(0,5,1,1),
        BentoBoxRect(0,6,1,1),
        BentoBoxRect(0,7,1,1),
      ))
    case Medium => BentoBoxData(
      BentoBoxSize(4,3),
      List(
        BentoBoxRect(0,0,2,1),
        BentoBoxRect(2,0,1,2),
        BentoBoxRect(3,0,1,1),
        BentoBoxRect(0,1,1,1),
        BentoBoxRect(1,1,1,1),
        BentoBoxRect(3,1,1,1),
        BentoBoxRect(0,2,2,1),
        BentoBoxRect(2,2,2,1),
      ))
    case Large => BentoBoxData(
      BentoBoxSize(4,3),
      List(
        BentoBoxRect(0,0,2,1),
        BentoBoxRect(2,0,1,2),
        BentoBoxRect(3,0,1,1),
        BentoBoxRect(0,1,1,1),
        BentoBoxRect(1,1,1,1),
        BentoBoxRect(3,1,1,1),
        BentoBoxRect(0,2,2,1),
        BentoBoxRect(2,2,2,1),
      ))
    case Extra => BentoBoxData(
      BentoBoxSize(4,3),
      List(
        BentoBoxRect(0,0,2,1),
        BentoBoxRect(2,0,1,2),
        BentoBoxRect(3,0,1,1),
        BentoBoxRect(0,1,1,1),
        BentoBoxRect(1,1,1,1),
        BentoBoxRect(3,1,1,1),
        BentoBoxRect(0,2,2,1),
        BentoBoxRect(2,2,2,1),
      ))
  }

  div(
    span("hello"),
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
      Navbar(),
      KeyboardInfo(keyboard),
      ImageSlider(keyboard.images),
      BigTitle(
        keyboard.name,
        """
          |Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          |Phasellus aliquet purus a fringilla condimentum. Praesent
          |vestibulum enim neque, eu placerat nulla molestie in
          | """.stripMargin
      ),
      //4x3
      child <-- boxes.map { boxData =>
        BentoBox(
          boxData.size,
          boxData.items.zip(boxItems).zipWithIndex.map { (tuple, index) =>
            val (rect, item) = tuple
            BentoBoxItem(s"area_${index}", rect, item)
          },
          width.percent := 100,
          height.rem := 50,
          padding.rem := 1,
        )
      },
      BigTitle("Technical Specifications"),
      ExpandableSpecs(keyboard.specs),
      BigTitle("Bill Of Materials"),
      BillOfMaterials(),
      Footer(),
    )
  )
}