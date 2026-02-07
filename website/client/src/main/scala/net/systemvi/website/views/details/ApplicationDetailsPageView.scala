package net.systemvi.website.views.details

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import io.circe.generic.*
import io.circe.generic.auto.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import net.systemvi.common.dtos.ApplicationDto
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