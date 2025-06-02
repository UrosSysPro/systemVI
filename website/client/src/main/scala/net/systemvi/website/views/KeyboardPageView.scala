package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.KeyboardPage
import net.systemvi.website.api.KeyboardApi
import net.systemvi.website.darkproject.bento_box.*
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.bill_of_materials.BillOfMaterials
import net.systemvi.website.darkproject.expandable_specs.ExpandableSpecs
import net.systemvi.website.darkproject.footer.Footer
import net.systemvi.website.darkproject.keyboard_info.KeyboardInfo
import net.systemvi.website.darkproject.navbar.Navbar
import net.systemvi.website.darkproject.section.{Section, SectionItem}
import net.systemvi.website.darkproject.slider.ImageSlider
import net.systemvi.website.model.*
import org.scalajs.dom

sealed trait ScreenSize(val width:Int)
object Small extends ScreenSize(500)
object Medium extends ScreenSize(1000)
object Large extends ScreenSize(1300)
object Extra extends ScreenSize(1500)


def KeyboardPageView(page:KeyboardPage):HtmlElement = {
  val keyboard=KeyboardApi.get(page.keyboardId)

  val screenSize=windowEvents(_.onResize).map(_=>dom.window.innerWidth match{
    case x if x < Small.width => Small
    case x if x < Medium.width => Medium
    case x if x < Large.width => Large
    case _ => Extra
  })

  val boxItems=List.range(0,8).map(_=>div(
    padding:="0.25rem",
    height.percent:=100,
    div(
      borderRadius.rem:=1,
      height.percent:=100,
      backgroundColor.rgb(246,246,246),
    )
  ))

  val boxSizes = screenSize.map{
    case Small => List(
     BentoBoxRect(0,0,2,1),
     BentoBoxRect(2,0,1,2),
     BentoBoxRect(3,0,1,1),
     BentoBoxRect(0,1,1,1),
     BentoBoxRect(1,1,1,1),
     BentoBoxRect(3,1,1,1),
     BentoBoxRect(0,2,2,1),
     BentoBoxRect(2,2,2,1),
    )
    case Medium => List(
      BentoBoxRect(0,0,2,1),
      BentoBoxRect(2,0,1,2),
      BentoBoxRect(3,0,1,1),
      BentoBoxRect(0,1,1,1),
      BentoBoxRect(1,1,1,1),
      BentoBoxRect(3,1,1,1),
      BentoBoxRect(0,2,2,1),
      BentoBoxRect(2,2,2,1),
    )
    case Large => List(
      BentoBoxRect(0,0,2,1),
      BentoBoxRect(2,0,1,2),
      BentoBoxRect(3,0,1,1),
      BentoBoxRect(0,1,1,1),
      BentoBoxRect(1,1,1,1),
      BentoBoxRect(3,1,1,1),
      BentoBoxRect(0,2,2,1),
      BentoBoxRect(2,2,2,1),
    )
    case Extra => List(
      BentoBoxRect(0,0,2,1),
      BentoBoxRect(2,0,1,2),
      BentoBoxRect(3,0,1,1),
      BentoBoxRect(0,1,1,1),
      BentoBoxRect(1,1,1,1),
      BentoBoxRect(3,1,1,1),
      BentoBoxRect(0,2,2,1),
      BentoBoxRect(2,2,2,1),
    )
  }

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
          BentoBoxItem("area_1",BentoBoxRect(0,0,2,1),boxItems(0)),
          BentoBoxItem("area_2",BentoBoxRect(2,0,1,2),boxItems(1)),
          BentoBoxItem("area_3",BentoBoxRect(3,0,1,1),boxItems(2)),
          BentoBoxItem("area_4",BentoBoxRect(0,1,1,1),boxItems(3)),
          BentoBoxItem("area_5",BentoBoxRect(1,1,1,1),boxItems(4)),
          BentoBoxItem("area_6",BentoBoxRect(3,1,1,1),boxItems(5)),
          BentoBoxItem("area_7",BentoBoxRect(0,2,2,1),boxItems(6)),
          BentoBoxItem("area_8",BentoBoxRect(2,2,2,1),boxItems(7)),
        ),
//        boxSizes.map(_.zip(boxItems).zipWithIndex.map{
//          case ((rect,element),index)=>
//            BentoBoxItem(s"area_$index",rect,element)
//        }),
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