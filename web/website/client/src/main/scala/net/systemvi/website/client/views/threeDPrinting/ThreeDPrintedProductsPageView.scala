package net.systemvi.website.client.views.threeDPrinting

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import io.circe.generic.*
import io.circe.generic.auto.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import net.systemvi.website.*
import net.systemvi.website.client.darkproject.big_title.BigTitle
import net.systemvi.website.client.darkproject.footer.Footer
import net.systemvi.website.client.darkproject.neo_navbar.NeoNavbar
import net.systemvi.website.client.services.ThreeDPrintedProductService
import net.systemvi.website.client.utils.Constants
import net.systemvi.website.client.darkproject.product_info.given
import org.scalajs.dom

import scala.concurrent.ExecutionContext


object ThreeDPrintedProductsPageView{
  given ExecutionContext = ExecutionContext.global

  def component(): HtmlElement = {
    val products = ThreeDPrintedProductService.getAll().startWith(List.empty)
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
