package net.systemvi.website.views

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import io.circe.generic.*
import io.circe.generic.auto.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import net.systemvi.common.dtos.*
import net.systemvi.website.*
import net.systemvi.website.darkproject.big_title.*
import net.systemvi.website.darkproject.bill_of_materials.*
import net.systemvi.website.darkproject.expandable_specs.*
import net.systemvi.website.darkproject.footer.*
import net.systemvi.website.darkproject.neo_navbar.*
import net.systemvi.website.darkproject.product_info.*
import net.systemvi.website.darkproject.product_info.given
import net.systemvi.website.darkproject.slider.*
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.utils.Constants
import org.scalajs.dom
import scala.concurrent.ExecutionContext
import net.systemvi.website.services.ThreeDPrintedProductService

given ExecutionContext = ExecutionContext.global

object ThreeDPrintedProductsPageView{
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
