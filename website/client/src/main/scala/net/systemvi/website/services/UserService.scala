package net.systemvi.website.services

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import org.scalajs.dom.{HttpMethod, RequestCredentials, RequestInit,Headers}
import io.circe.*
import io.circe.syntax.*
import io.circe.scalajs.*
import io.circe.generic.auto.*


import net.systemvi.common.dtos.*
import net.systemvi.website.utils.Constants

object UserService {
  import scala.concurrent.ExecutionContext

  given ExecutionContext = ExecutionContext.global

  def whoAmI: EventStream[UserDto] ={
    val headers = Headers()
    val init = new RequestInit {}
    init.headers = headers
    init.credentials = RequestCredentials.include
    init.method = HttpMethod.GET

    EventStream.fromFuture(
      dom.fetch(s"${Constants.serverUrl}/user", init)
        .toFuture
        .flatMap(_.json().toFuture)
        .map(decodeJs[UserDto](_).getOrElse(throw Exception()))
    )
  }
}
