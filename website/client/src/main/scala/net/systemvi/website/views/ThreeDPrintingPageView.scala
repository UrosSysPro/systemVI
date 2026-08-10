package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.darkproject.Imports.*
import net.systemvi.website.routes.Pages.*
import java.util.UUID

object ThreeDPrintingPageView{

  def component(): HtmlElement = {
    div(
      display.flex, flexDirection.column, alignItems.center, paddingTop.rem(6),
      div(
        display.flex, flexDirection.column, justifyContent.start, width.percent(100), maxWidth.px(1450),
        NeoNavbar(),
        BigTitle("3D Printing",""),
        // Section(
        //   title = "",
        //   items = List(1,2,3).map{ product =>
        //     SectionItem(
        //       name = s"Product ${product}",
        //       "",
        //       page = ThreeDPrintedProductDetailsPage(UUID.randomUUID())
        //     )
        //   },
        //   viewAllPage = HomePage

        // ),
        Footer(),
      )
    )
  }

}
