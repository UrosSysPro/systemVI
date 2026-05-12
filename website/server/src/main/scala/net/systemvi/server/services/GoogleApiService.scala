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


trait GoogleApiService[F[_]] {

  case class GoogleTokenResponse(
                                  access_token: String,
                                  expires_in: Int,
                                  scope: String,
                                  token_type: String,
                                )

  case class GoogleUserProfile(

                              )

  def getAccessToken(context: AppContext[F], code: String): F[GoogleTokenResponse]
  def getUserProfile(context: AppContext[F], accessToken: String): F[GoogleUserProfile]
}

object GoogleApiService {
  def make[F[_]: Async]: GoogleApiService[F] = new GoogleApiService[F] {

    override def getAccessToken(context: AppContext[F], code: String): F[GoogleTokenResponse] = {
      val client = EmberClientBuilder
        .default[F]
        .build

      val uriService = GoogleUriService.make[F]

      client.use{ client => for{
        uri <- uriService.getAccessTokenUri(context, code)
        request <- Async[F].delay{ Request[F](Method.POST, uri) }
        response <- client.expect[GoogleTokenResponse](request)
      } yield response }
    }

    override def getUserProfile(context: AppContext[F], accessToken: String): F[GoogleUserProfile] = ???
  }
}