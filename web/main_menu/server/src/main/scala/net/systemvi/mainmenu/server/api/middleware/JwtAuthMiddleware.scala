package net.systemvi.mainmenu.server.api.middleware

import cats.effect.*
import dev.profunktor.auth.jwt.*
import io.circe.parser.decode
import net.systemvi.mainmenu.common.dto.UserDto
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import org.http4s.server.AuthMiddleware
import pdi.jwt.*

/**
 * Authenticates a request from its bearer token and yields the session user
 * stored in the claim.
 *
 * The raw request is expected to carry the token in an `Authorization` header;
 * for cookie-based browsers, wrap the routes with [[copyCookieToHeader]] first.
 */
def jwtAuth[F[_]: Async](using context: AppContext[F]): AuthMiddleware[F, UserDto] = {
  val jwtAuth = JwtAuth.hmac(context.config.jwtAuth.secret.toCharArray, JwtAlgorithm.HS256)

  val authenticate: JwtToken => JwtClaim => F[Option[UserDto]] =
    _ => claim => Async[F].delay(decode[UserDto](claim.content).toOption)

  dev.profunktor.auth.JwtAuthMiddleware[F, UserDto](
    jwtAuth = jwtAuth,
    authenticate = authenticate,
  )
}
