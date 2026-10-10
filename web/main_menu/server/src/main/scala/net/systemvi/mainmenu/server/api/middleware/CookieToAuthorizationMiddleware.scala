package net.systemvi.mainmenu.server.api.middleware

import cats.data.Kleisli
import org.http4s.*
import org.http4s.headers.*

private val SessionCookieName = "access_token"

/**
 * Copies the session cookie into an `Authorization: Bearer` header, so routes
 * behind [[jwtAuth]] can read the token the browser holds.
 *
 * If there is no cookie the request is passed through untouched, which lets the
 * auth middleware reject it as unauthenticated.
 */
def copyCookieToHeader[F[_]](service: HttpRoutes[F]): HttpRoutes[F] = Kleisli { request =>
  request.cookies.find(_.name == SessionCookieName) match {
    case Some(cookie) =>
      service(request.putHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, cookie.content))))
    case None =>
      service(request)
  }
}
