package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.common.dtos.ApplicationDto
import net.systemvi.website.*
import net.systemvi.website.darkproject.big_title.*
import net.systemvi.website.darkproject.footer.*
import net.systemvi.website.darkproject.neo_navbar.*
import net.systemvi.website.darkproject.slider.*
import net.systemvi.website.darkproject.section.*
import net.systemvi.website.darkproject.slider.*
import net.systemvi.website.darkproject.product_info.*
import net.systemvi.website.darkproject.expandable_specs.*
import net.systemvi.website.darkproject.bill_of_materials.*
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import org.scalajs.dom

import scala.concurrent.ExecutionContext

given ExecutionContext = ExecutionContext.global

def ApplicationView(page: GamePage): HtmlElement = {
  val applicationStream = EventStream.fromFuture(
    dom.fetch(s"${Constants.serverUrl}/applications")
      .toFuture
      .flatMap(_.json().toFuture)
      .map(decodeJs[ApplicationDto](_))
      .map(_.getOrElse(throw Exception("error parsing json")))
  )

  div(
    className("flex flex-col items-center pt-24"),
    child <-- applicationStream.map{ app =>
      div(
        className("flex flex-col justify-start w-full max-w-[1450px]"),
        child <-- applicationStream.map { a => BigTitle(a.name) },
        NeoNavbar(),
//        ProductInfo(app),
//        ImageSlider(game.images),
        BigTitle("Technical Specifications"),
//        ExpandableSpecs(game.specs),
        BigTitle("Bill Of Materials"),
        BillOfMaterials(),
        Footer(),
      )
    }
  )
}