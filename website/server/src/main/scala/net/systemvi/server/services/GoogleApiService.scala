package net.systemvi.server.services

import net.systemvi.server.persistance.contexts.AppContext
import cats.*
import cats.implicits.*
import cats.effect.*
import io.circe.*
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.ember.client.*
import org.http4s.headers.Authorization


trait GoogleApiService[F[_]] {

  case class GoogleTokenResponse(
                                  access_token: String,
                                  expires_in: Int,
                                  scope: String,
                                  token_type: String,
                                )

  case class GoogleUserProfile(
                                sub: String,           // unique user ID
                                email: String,
                                email_verified: Boolean,
                                name: Option[String],
                                picture: Option[String],
                                given_name: Option[String],
                                family_name: Option[String],
                                locale: Option[String]
                              )

  def getAccessToken(context: AppContext[F], code: String): F[GoogleTokenResponse]
  def getUserProfile(context: AppContext[F], accessToken: String): F[GoogleUserProfile]
}

object GoogleApiService {
  def make[F[_]: Async]: GoogleApiService[F] = new GoogleApiService[F] {

    override def getAccessToken(context: AppContext[F], code: String): F[GoogleTokenResponse] = {
      val client = context.httpClient
      val uriService = context.googleUriService

      for{
        uri <- uriService.getAccessTokenUri(context, code)
        request <- Async[F].delay{ Request[F](Method.POST, uri) }
        response <- client.expect[GoogleTokenResponse](request)
      } yield response
    }

    override def getUserProfile(context: AppContext[F], accessToken: String): F[GoogleUserProfile] = {
      val client = context.httpClient
      val uriService = context.googleUriService

      for{
        uri <- uriService.getUserProfileUri(context)
        request <- Async[F].delay{ Request[F](
          method = Method.POST,
          uri = uri,
          headers = Headers(
            Authorization(Credentials.Token(AuthScheme.Bearer, accessToken)),
          )
        )}
        response <- client.expect[GoogleUserProfile](request)
      } yield response
    }
  }
}