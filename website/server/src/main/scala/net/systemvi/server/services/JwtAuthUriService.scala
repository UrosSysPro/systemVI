package net.systemvi.server.services

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.UriTemplate.*

import net.systemvi.server.persistance.contexts.AppContext

trait JwtAuthUriService[F[_]] {
  def getUserProfilePageUrl(context: AppContext[F]): F[Uri]
}

object JwtAuthUriService {
  def create[F[_]: Async]: JwtAuthUriService[F] = (context: AppContext[F]) => {
    val host = context.config.server.clientUrl.host.getOrElse{throw Exception()}
    val port = context.config.server.clientUrl.port
    Async[F].delay {
      UriTemplate(
        authority = Some(Uri.Authority(host = host,port = port)),
        scheme = Some(Uri.Scheme.http),
        path = List(PathElm("user-profile"))
      ).toUriIfPossible.getOrElse(throw Exception())
    }
  }
}
