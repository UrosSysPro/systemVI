package net.systemvi.main_menu.client.api

import com.raquo.laminar.api.L.*
import io.circe.parser.decode
import net.systemvi.mainmenu.common.dto.UserDto
import net.systemvi.main_menu.client.utils.Constants
import org.scalajs.dom
import org.scalajs.dom.{HttpMethod, RequestCredentials, RequestInit}

import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Thenable.Implicits.*

object AuthApi {

  given ExecutionContext = ExecutionContext.global

  /** Where to send the browser to start the Google sign-in flow. */
  val googleSignInUrl: String = s"${Constants.serverUrl}/auth/google/redirect"

  /** The signed-in user, or `None` when there is no valid session cookie. */
  def currentUser: EventStream[Option[UserDto]] =
    EventStream.fromFuture(
      dom.fetch(s"${Constants.serverUrl}/user", withCredentials).toFuture.flatMap { response =>
        if response.ok then
          response.text().toFuture.map(body => decode[UserDto](body).toOption)
        else
          Future.successful(None)
      }
    )

  /**
   * The session as a stream, so a view can tell "still checking" from "signed
   * out" before deciding where to send the user: `None` while loading,
   * `Some(None)` when signed out, `Some(Some(user))` when signed in.
   */
  def session: EventStream[Option[Option[UserDto]]] = currentUser.map(Some(_))

  private def withCredentials: RequestInit = {
    val init = new RequestInit {}
    init.method = HttpMethod.GET
    init.credentials = RequestCredentials.include
    init
  }
}
