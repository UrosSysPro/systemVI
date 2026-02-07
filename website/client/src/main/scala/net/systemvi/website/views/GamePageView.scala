package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import io.circe.scalajs.decodeJs
import net.systemvi.common.dtos.ApplicationDto
import net.systemvi.website.Constants
import net.systemvi.website.api.GameApi
import net.systemvi.website.darkproject.neo_navbar.*
import net.systemvi.website.darkproject.product_info.*
import net.systemvi.website.darkproject.product_info.given
import net.systemvi.website.darkproject.big_title.BigTitle
import net.systemvi.website.darkproject.expandable_specs.*
import net.systemvi.website.darkproject.bill_of_materials.*
import net.systemvi.website.darkproject.footer.*
import org.scalajs.dom
import cats.*
import cats.implicits.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import io.circe.generic.*
import io.circe.generic.auto.*
import net.systemvi.website.*
import net.systemvi.website.routes.Pages.*
import org.scalajs.dom

def GamePageView(page:GamePage):HtmlElement={

  val appEventStream = EventStream.fromFuture(
    dom.fetch(s"${Constants.serverUrl}/games")
      .toFuture
      .flatMap(_.json().toFuture)
      .map(decodeJs[Option[ApplicationDto]](_))
      .map(_.getOrElse(throw Exception()))
      .map(_.getOrElse(throw Exception()))
  )

  ApplicationPageView(
       appEventStream
  )
//  val game=GameApi.get(page.gameId)
//  div(
//    cls:="flex flex-col items-center pt-24",
//    div(
//      className:="flex flex-col justify-start w-full max-w-[1450px]",
//      BigTitle(
//        game.name,
//      ),
//      NeoNavbar(),
//      ProductInfo(game),
////      ImageSlider(game.images),
//      BigTitle("Technical Specifications"),
//      ExpandableSpecs(game.specs),
//      BigTitle("Bill Of Materials"),
//      BillOfMaterials(),
//      Footer(),
//    )
//  )
}