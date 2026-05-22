package net.systemvi.server.api.middleware

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import dev.profunktor.auth.JwtAuthMiddleware
import dev.profunktor.auth.jwt.*
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.persistance.models.User
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.AuthMiddleware
import pdi.jwt.*

def jwtAuth[F[_]: Async](using context: AppContext[F]): AuthMiddleware[F,User] = {
  val jwtAuth = JwtAuth.hmac(context.config.jwtAuthConfig.secret.toCharArray, JwtAlgorithm.HS256)

  val authenticate: JwtToken => JwtClaim => F[Option[User]] =
    (token: JwtToken) =>
      (claim: JwtClaim) => decode[User](claim.content) match {
        case Right(user) => Async[F].delay {
          user.some
        }
        case Left(_) => Async[F].delay {
          None
        }
      }

  val middleware = dev.profunktor.auth.JwtAuthMiddleware[F, User](
    jwtAuth = jwtAuth,
    authenticate = authenticate,
  )

  middleware
}