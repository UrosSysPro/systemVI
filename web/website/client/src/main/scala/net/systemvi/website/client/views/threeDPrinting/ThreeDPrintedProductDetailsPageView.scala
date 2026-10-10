package net.systemvi.website.client.views.threeDPrinting

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import io.circe.generic.*
import io.circe.generic.auto.*
import io.circe.scalajs.*
import io.circe.scalajs.EncoderJsOps.*
import net.systemvi.website.*
import net.systemvi.website.client.utils.Constants
import net.systemvi.website.client.darkproject.product_info.given
import org.scalajs.dom
import scala.concurrent.ExecutionContext
import net.systemvi.website.client.routes.Pages.*

given ExecutionContext = ExecutionContext.global

def ThreeDPrintedProductDetailsPageView(page: ThreeDPrintedProductDetailsPage): HtmlElement = {
  div("hello")
}
