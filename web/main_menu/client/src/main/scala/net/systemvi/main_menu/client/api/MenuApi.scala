package net.systemvi.main_menu.client.api

import com.raquo.laminar.api.L.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.decode
import net.systemvi.mainmenu.common.dto.{CategoryDto, ItemDto}
import net.systemvi.main_menu.client.utils.Constants
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scala.scalajs.js.Thenable.Implicits.*

object MenuApi {

  given ExecutionContext = ExecutionContext.global
  given Decoder[ItemDto] = deriveDecoder

  /** The full category forest; each category carries its nested children. */
  def categories: EventStream[List[CategoryDto]] =
    fetchJson[List[CategoryDto]](s"${Constants.serverUrl}/category")

  def items: EventStream[List[ItemDto]] =
    fetchJson[List[ItemDto]](s"${Constants.serverUrl}/items")

  private def fetchJson[A](url: String)(using decoder: Decoder[A]): EventStream[A] =
    EventStream.fromFuture(
      dom.fetch(url).toFuture
        .flatMap(_.text().toFuture)
        .map(body => decode[A](body).fold(error => throw error, identity))
    )
}
