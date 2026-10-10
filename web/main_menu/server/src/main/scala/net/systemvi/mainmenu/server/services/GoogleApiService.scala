package net.systemvi.mainmenu.server.services

import cats.effect.*
import io.circe.*
import io.circe.generic.semiauto.deriveDecoder
import net.systemvi.mainmenu.server.config.Config
import org.http4s.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.client.Client
import org.http4s.headers.Authorization

case class GoogleTokenResponse(
  access_token: String,
  expires_in: Int,
  scope: String,
  token_type: String,
)

object GoogleTokenResponse {
  given Decoder[GoogleTokenResponse] = deriveDecoder
}

/** The subset of the OpenID Connect userinfo response we care about. */
case class GoogleUserProfile(
  sub: String,
  email: String,
  email_verified: Boolean,
  name: Option[String],
  picture: Option[String],
  given_name: Option[String],
  family_name: Option[String],
  locale: Option[String],
)

object GoogleUserProfile {
  given Decoder[GoogleUserProfile] = deriveDecoder
}

trait GoogleApiService[F[_]] {
  def getAccessToken(code: String): F[GoogleTokenResponse]
  def getUserProfile(accessToken: String): F[GoogleUserProfile]
}

object GoogleApiService {
  def make[F[_]: Async](client: Client[F], config: Config, uris: GoogleUriService): GoogleApiService[F] =
    new GoogleApiService[F] {

      override def getAccessToken(code: String): F[GoogleTokenResponse] = {
        // The client secret goes in the request body, not the query string.
        val form = UrlForm(
          "code" -> code,
          "grant_type" -> "authorization_code",
          "redirect_uri" -> uris.callbackUri.renderString,
          "client_id" -> config.googleAuth.clientId,
          "client_secret" -> config.googleAuth.clientSecret.value,
        )
        client.expect[GoogleTokenResponse](
          Request[F](Method.POST, uris.accessTokenUri).withEntity(form)
        )
      }

      override def getUserProfile(accessToken: String): F[GoogleUserProfile] =
        client.expect[GoogleUserProfile](
          Request[F](Method.GET, uris.userProfileUri)
            .putHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, accessToken)))
        )
    }
}
