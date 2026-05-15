package net.systemvi.server.services

import net.systemvi.server.persistance.contexts.AppContext
import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.UriTemplate.*

trait GoogleUriService[F[_]] {
  def getLoginRedirectUri(context: AppContext[F]): F[Uri]
  def getAccessTokenUri(context: AppContext[F], code: String): F[Uri]
  def getUserProfileUri(context: AppContext[F]): F[Uri]
}

object GoogleUriService {
  def make[F[_]: Async]: GoogleUriService[F] = new GoogleUriService[F] {
    private sealed trait GoogleScope(val value: String)

    private object DriveMetadataReadonly extends GoogleScope("https://www.googleapis.com/auth/drive.metadata.readonly")

    private object CalendarReadonly extends GoogleScope("https://www.googleapis.com/auth/calendar.readonly")

    private object Profile extends GoogleScope("https://www.googleapis.com/auth/userinfo.profile")

    private object Email extends GoogleScope("https://www.googleapis.com/auth/userinfo.email")

    private object OpenID extends GoogleScope("openid")

    private sealed trait GoogleAccessType(val value: String)

    private object Online extends GoogleAccessType("online")

    private object Offline extends GoogleAccessType("offline")

    private sealed trait ResponseType(val value: String)

    private object Code extends ResponseType("code")

    override def getLoginRedirectUri(context: AppContext[F]): F[Uri] = {
      val scheme = Uri.Scheme.https
      val domain = "accounts.google.com"
      val path = "o/oauth2/v2/auth".split("/").map(PathElm.apply).toList

      val scope = s"${Email.value} ${Profile.value} ${OpenID.value}"
      val accessType = Offline.value
      val includeGrantedScopes = true
      val responseType = Code.value
      val state = "state_parameter_passthrough_value"
      val redirectUri = "http://localhost:8080/api/auth/google/callback"
      val clientId = context.config.googleAuthConfig.clientId


      for{
        url <- Async[F].delay{
          UriTemplate(
            scheme = scheme.some,
            authority = Uri.Authority(host = Uri.RegName(domain)).some,
            path = path,
            query = List(
              ParamElm("scope", scope),
              ParamElm("access_type", accessType),
              ParamElm("include_granted_scopes", includeGrantedScopes.toString),
              ParamElm("response_type", responseType),
              ParamElm("state", state),
              ParamElm("redirect_uri", redirectUri),
              ParamElm("client_id", clientId),
            )
          )
        }
      } yield url.toUriIfPossible.getOrElse(throw Exception())
    }

    override def getAccessTokenUri(context: AppContext[F], code: String): F[Uri] = {
      val scheme = Uri.Scheme.https
      val domain = "oauth2.googleapis.com"
      val path = List(PathElm("token"))

      val grant_type = "authorization_code"
      val clientId = context.config.googleAuthConfig.clientId
      val clientSecret = context.config.googleAuthConfig.clientSecret.value
      val redirectUri = "http://localhost:8080/api/auth/google/callback"

      for{
        urlTemplate <- Async[F].delay{
          UriTemplate(
            scheme = scheme.some,
            authority = Uri.Authority(host = Uri.RegName(domain)).some,
            path = path,
            query = List(
              ParamElm("code", code),
              ParamElm("grant_type", grant_type),
              ParamElm("redirect_uri", redirectUri),
              ParamElm("client_id", clientId),
              ParamElm("client_secret", clientSecret),
            )
          )
        }
      } yield urlTemplate.toUriIfPossible.getOrElse(throw Exception())
    }

    override def getUserProfileUri(context: AppContext[F]): F[Uri] = {
      Async[F].delay{ uri"https://openidconnect.googleapis.com/v1/userinfo" }
    }
  }
}
