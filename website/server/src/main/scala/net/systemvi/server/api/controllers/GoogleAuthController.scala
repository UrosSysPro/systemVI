package net.systemvi.server.api.controllers

import cats.*
import cats.effect.*
import cats.effect.std.Env
import cats.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import fs2.*
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.persistance.models.Manufacturer
import net.systemvi.server.services.*
import net.systemvi.common.dtos.*
import net.systemvi.server.persistance.models.*
import org.http4s.*
import org.http4s.UriTemplate.{ParamElm, PathElm, toUri}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*
import org.http4s.ember.client.EmberClientBuilder

import java.util.UUID
import scala.util.*

/**
 * https://accounts.google.com/o/oauth2/v2/auth?
 * scope=https%3A//www.googleapis.com/auth/drive.metadata.readonly%20https%3A//www.googleapis.com/auth/calendar.readonly&
 * access_type=offline&
 * include_granted_scopes=true&
 * response_type=code&
 * state=state_parameter_passthrough_value&
 * redirect_uri=https%3A//developers.google.com/oauthplayground&
 * client_id=client_id
 * */

object GoogleAuthLinks {
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


  def getRedirectLink(context: AppContext[IO]): IO[Uri] = {

    val scheme = Uri.Scheme.https
    val domain = "accounts.google.com"
    val path = "o/oauth2/v2/auth".split("/").map(PathElm.apply).toList

    val scope = s"${Email.value} ${Profile.value} ${OpenID.value}"
    //    val scope = DriveMetadataReadonly.value
    val accessType = Offline.value
    val includeGrantedScopes = true
    val responseType = Code.value
    val state = "state_parameter_passthrough_value"
    //    val redirectUri = "https://developers.google.com/oauthplayground"
    val redirectUri = "http://localhost:8080/api/auth/google/callback"
    val clientId = context.config.googleAuthConfig.clientId


    for{
      url <- IO{
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
      _ <- context.logger.info(scope)
    } yield url.toUriIfPossible.getOrElse(throw Exception())
  }

  def getTokenLink(context: AppContext[IO], code: String): IO[Uri] = {
    val scheme = Uri.Scheme.https
    val domain = "oauth2.googleapis.com"
    val path = List(PathElm("token"))

    val grant_type = "authorization_code"
    val clientId = context.config.googleAuthConfig.clientId
    val clientSecret = context.config.googleAuthConfig.clientSecret.value
    val redirectUri = "http://localhost:8080/api/auth/google/callback"

    for{
      urlTemplate <- IO{
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

  def getProfileLink(context: AppContext[IO], accessToken: String): IO[Uri] = {
    val scheme = Uri.Scheme.https
    val domain = "oauth2.googleapis.com"
    val path = List(PathElm("token"))

    val grant_type = "authorization_code"
    val clientId = context.config.googleAuthConfig.clientId
    val clientSecret = context.config.googleAuthConfig.clientSecret.value
    val redirectUri = "http://localhost:8080/api/auth/google/callback"

    for{
      urlTemplate <- IO{
        UriTemplate(
          scheme = scheme.some,
          authority = Uri.Authority(host = Uri.RegName(domain)).some,
          path = path,
          query = List(
//            ParamElm("code", code),
            ParamElm("grant_type", grant_type),
            ParamElm("redirect_uri", redirectUri),
            ParamElm("client_id", clientId),
            ParamElm("client_secret", clientSecret),
          )
        )
      }
    } yield urlTemplate.toUriIfPossible.getOrElse(throw Exception("nis"))
  }
}


class GoogleAuthController(context: AppContext[IO]) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO]{

    case request @ GET -> Root / "redirect" => for{
      url <- GoogleAuthLinks.getRedirectLink(context)
      response <- Ok(url.show)
    } yield response

    case request@GET -> Root / "callback" :? query => {

      val code = query("code").toList.head

      case class GoogleTokenResponse(
                                    access_token: String,
                                    expires_in: Int,
                                    scope: String,
                                    token_type: String,
                                    )
      case class GoogleUserProfile(

                                  )

      val clientResource = EmberClientBuilder
        .default[IO]
        .build

      val result = clientResource.use{client=>
        for {
          _ <- context.logger.info(code)

          uri <- GoogleAuthLinks.getTokenLink(context,code)
          tokenResponse <- client.expect[GoogleTokenResponse](Request[IO](Method.POST, uri))
          accessToken = tokenResponse.access_token

//          getUserProfileLink <- GoogleAuthLinks.getProfileLink(context,accessToken)
//          profileResponse <- client.expect[GoogleUserProfile](Request[IO](Method.POST, getUserProfileLink))

          _ <- context.logger.info(tokenResponse.access_token)

          response <- Ok("callback")
        } yield response
      }
      
      result.attempt.flatMap {
        case Left(throwable) => Ok(s"fail ${throwable.getMessage}")
        case Right(result) => Ok("success")
      }
    }
  }
}
