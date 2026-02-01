package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.api.ApplicationApi
import net.systemvi.website.darkproject.bento_box.{*,given}
import org.scalajs.dom
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.neo_navbar.*
import net.systemvi.website.darkproject.product_info.{ProductInfo, given}
import net.systemvi.website.darkproject.expandable_specs.ExpandableSpecs
import net.systemvi.website.darkproject.bill_of_materials
import net.systemvi.website.darkproject.slider.ImageSlider
import net.systemvi.website.darkproject.footer.Footer

def BentoCard(title:String): HtmlElement = {
  div(
    padding.rem:=0.5f,
    width.percent:=100,
    height.percent:=100,
    div(
      title,
      display.flex,
      justifyContent.center,
      alignItems.center,
      fontWeight.bold,
      backgroundColor:="#eee",
      borderRadius.rem:=1,
      width.percent:=100,
      height.percent:=100,
    )
  )
}

def ConfiguratorPageView():HtmlElement={
  val application=ApplicationApi.get(0)
  application.map{app=>
    div(
      display.flex,
      flexDirection.column,
      alignItems.center,
      paddingTop.rem:=6,
      div(
        className := "max-w-[1450px]",
        display.flex,
        flexDirection.column,
        justifyContent.start,
        width.percent:=100,

        NeoNavbar(),
        BigTitle(app.name),
        ProductInfo(app),
        ImageSlider(app.screenshots),
        BentoBox(
          BentoBoxSize(4,3),
          List(
            BentoBoxItem(
              "area_1",
              BentoBoxRect(
                0,0,2,1
              ),
              BentoCard("Support any layout"),
            ),
            BentoBoxItem(
              "area_2",
              BentoBoxRect(
                2,0,1,2
              ),
              BentoCard("Customizable Keycaps"),
            ),
            BentoBoxItem(
              "area_3",
              BentoBoxRect(
                3,0,1,1
              ),
              BentoCard("Material Design"),
            ),
            BentoBoxItem(
              "area_4",
              BentoBoxRect(
                3,1,1,1
              ),
              BentoCard("Cross Platform"),
            ),
            BentoBoxItem(
              "area_5",
              BentoBoxRect(
                0,1,1,1
              ),
              BentoCard("Keyboard Tester"),
            ),
            BentoBoxItem(
              "area_6",
              BentoBoxRect(
                1,1,1,1
              ),
              BentoCard("Macros"),
            ),
            BentoBoxItem(
              "area_7",
              BentoBoxRect(
                0,2,2,1
              ),
              BentoCard("Themes"),
            ),
            BentoBoxItem(
              "area_8",
              BentoBoxRect(
                2,2,2,1
              ),
              BentoCard("Save and Load"),
            ),
          ),
          width.percent:=100,
          height.rem:=60,
          paddingLeft.rem:=1,
          paddingRight.rem:=1,
        ),
        Footer(),
      )
    )
  }.getOrElse(div("keyboard not found"))
}
