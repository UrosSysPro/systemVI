package net.systemvi.server.api.middleware

import cats.*
import cats.implicits.*
import cats.data.Kleisli
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.headers.*
import org.http4s.headers.Authorization.given
import org.http4s.dsl.io.*
import org.slf4j.LoggerFactory

def copyCookieToHeader[F[_]](service: HttpRoutes[F]): HttpRoutes[F] = Kleisli { (req: Request[F]) =>
  val logger = LoggerFactory.getLogger("copy middleware")

  val tokenExtractor: Request[F] => Option[String] =
    request =>
      request.cookies
        .find(_.name == "access_token")
        .map(_.content)


  val accessToken = tokenExtractor(req).getOrElse("")

  tokenExtractor(req) match {
    case Some(token) => logger.info("has token")
    case None => logger.info("no token")
  }

  val requestWithHeader: Request[F] = req.putHeaders(
    Headers(Authorization(Credentials.Token(AuthScheme.Bearer,accessToken)))
  )

  service(requestWithHeader)
}
