package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import io.circe.syntax.*
import net.systemvi.mainmenu.common.dto.UserDto
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.persistance.models.{AuthIdentity, AuthProvider, Role, User, UserStatus}
import net.systemvi.mainmenu.server.services.GoogleUserProfile
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.*

import java.time.Instant
import java.util.UUID

/**
 * Google sign-in: `/auth/google/redirect` sends the browser to Google, and
 * `/auth/google/callback` turns Google's authorization code into a session.
 */
def googleAuthController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val uris = context.googleUriService
  val googleApi = context.googleApiService
  val users = context.db.users
  val identities = context.db.authIdentities
  val jwt = context.jwtService
  val logger = context.logger

  /** Finds the user behind a Google subject, creating a customer on first sign-in. */
  def resolveUser(profile: GoogleUserProfile): IO[User] =
    identities.getByProviderSubject(AuthProvider.Google, profile.sub).flatMap {
      case Some(identity) =>
        users.get(identity.userId).flatMap {
          case Some(user) => IO.pure(user)
          case None =>
            IO.raiseError(
              new IllegalStateException(
                s"Auth identity ${identity.uuid} points at a missing user ${identity.userId}"
              )
            )
        }
      case None =>
        val user = User(UUID.randomUUID(), Role.Customer, UserStatus.Active, profile.name, Some(profile.email))
        for {
          _ <- users.add(user)
          _ <- identities.add(
            AuthIdentity(UUID.randomUUID(), user.uuid, AuthProvider.Google, profile.sub, Some(profile.email))
          )
        } yield user
    }

  HttpRoutes.of[IO] {
    case GET -> Root / "redirect" =>
      Found(Location(uris.loginRedirectUri))

    case GET -> Root / "callback" :? query =>
      query.get("code").flatMap(_.headOption) match {
        case None =>
          BadRequest("Missing 'code' query parameter")

        case Some(code) =>
          val signIn = for {
            token <- googleApi.getAccessToken(code)
            profile <- googleApi.getUserProfile(token.access_token)
            user <- resolveUser(profile)
            session = UserDto(user.uuid, user.role.name, user.status.name, user.name, user.email)
            token = jwt.encode(session.asJson.noSpaces)
            cookie = ResponseCookie(
              name = "access_token",
              content = token,
              expires = Some(HttpDate.unsafeFromInstant(Instant.now.plusSeconds(jwt.ttl.toSeconds))),
              httpOnly = true,
              secure = true,
              path = Some("/"),
            )
            response <- Found(Location(context.config.server.clientUrl / "welcome")).map(_.addCookie(cookie))
          } yield response

          signIn.attempt.flatMap {
            case Right(response) => IO.pure(response)
            case Left(error) =>
              logger.error(error)(s"Google sign-in failed: ${error.getMessage}") *>
                InternalServerError("Google sign-in failed")
          }
      }
  }
}
