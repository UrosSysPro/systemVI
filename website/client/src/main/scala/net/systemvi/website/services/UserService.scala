package net.systemvi.website.services

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import net.systemvi.website.utils.Constants
import org.scalajs.dom.{HttpMethod, RequestCredentials, RequestInit,Headers}

import scala.scalajs.js


object UserService {
  import scala.concurrent.ExecutionContext

  given ExecutionContext = ExecutionContext.global

  def whoAmI: EventStream[js.Any] ={
    val headers = Headers()
    val init = new RequestInit {}
    init.headers = headers
    init.credentials = RequestCredentials.include
    init.method = HttpMethod.GET

    EventStream.fromFuture(
      dom.fetch(s"${Constants.serverUrl}/user", init)
        .toFuture
        .flatMap(_.json().toFuture)
    )
  }
}
