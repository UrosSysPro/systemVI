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
import net.systemvi.website.darkproject.section.*
import net.systemvi.website.darkproject.slider.*
import org.scalajs.dom


def ApplicationDetailsPageView(appSignal: EventStream[ApplicationDto]): HtmlElement = {

  div(
    cls:="flex flex-col items-center pt-24",
    div(
      className:="flex flex-col justify-start w-full max-w-[1450px]",
//      BigTitle(
//        game.name,
//      ),
      NeoNavbar(),
//      ProductInfo(game),
      //      ImageSlider(game.images),
      BigTitle("Technical Specifications"),
//      ExpandableSpecs(game.specs),
      BigTitle("Bill Of Materials"),
      BillOfMaterials(),
      Footer(),
    )
  )
}