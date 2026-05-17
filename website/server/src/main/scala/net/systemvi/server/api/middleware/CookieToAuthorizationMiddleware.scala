package net.systemvi.server.api.middleware

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.data.Kleisli
import cats.implicits.*
import org.http4s.*
import org.http4s.headers.Authorization
import org.http4s.implicits.*

def cookieToAuthorizationMiddleware(service: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
  val tokenExtractor: Request[IO] => Option[String] =
    request =>
      request.cookies
        .find(_.name == "access_token")
        .map(_.content)

  val accessToken = tokenExtractor(req).getOrElse("")

  service(
    req.addHeader(
      Authorization(Credentials.Token(AuthScheme.Bearer,accessToken))
    )
  )
}
