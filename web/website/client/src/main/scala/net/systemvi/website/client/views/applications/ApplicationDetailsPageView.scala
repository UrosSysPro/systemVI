package net.systemvi.website.client.views.applications

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import io.circe.generic.*
import io.circe.generic.auto.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import net.systemvi.website.*
import net.systemvi.website.client.darkproject.big_title.BigTitle
import net.systemvi.website.client.darkproject.bill_of_materials.BillOfMaterials
import net.systemvi.website.client.darkproject.expandable_specs.ExpandableSpecs
import net.systemvi.website.client.darkproject.footer.Footer
import net.systemvi.website.client.darkproject.neo_navbar.NeoNavbar
import net.systemvi.website.client.darkproject.product_info.ProductInfo
import net.systemvi.website.client.darkproject.slider.ImageSlider
import net.systemvi.website.client.utils.Constants
import net.systemvi.website.client.darkproject.product_info.given
import net.systemvi.website.client.routes.Pages.*
import net.systemvi.website.common.dtos.ApplicationDto
import org.scalajs.dom

import scala.concurrent.ExecutionContext

given ExecutionContext = ExecutionContext.global

def ApplicationDetailsPageView(page: ApplicationDetailsPage): HtmlElement = {

  val applicationSignal = EventStream.fromFuture(
    dom.fetch(s"${Constants.serverUrl}/applications/${page.applicationUUID}")
      .toFuture
      .flatMap(_.json().toFuture)
      .map(decodeJs[ApplicationDto](_))
      .map(_.getOrElse(throw Exception()))
  )

  div(
    cls:="flex flex-col items-center pt-24",
    child <-- applicationSignal.map{ app =>
      div(
        className := "flex flex-col justify-start w-full max-w-[1450px]",
        BigTitle(
          app.name,
        ),
        NeoNavbar(),
        ProductInfo(app),
        ImageSlider(app.images.map(_.imageUrl)),
        BigTitle("Technical Specifications"),
        ExpandableSpecs(List.empty),
        BigTitle("Bill Of Materials"),
        BillOfMaterials(),
        Footer(),
      )
    }
  )
}