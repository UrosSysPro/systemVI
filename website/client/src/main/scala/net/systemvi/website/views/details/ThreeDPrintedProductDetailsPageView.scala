package net.systemvi.website.views.details

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

given ExecutionContext = ExecutionContext.global

def ThreeDPrintedProductDetailsPageView(): HtmlElement = {
  div("hello")
}
