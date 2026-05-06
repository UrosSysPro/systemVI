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
import org.http4s.UriTemplate.{ParamElm, PathElm}
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*

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

object GoogleAuthRedirectLink {
  private sealed trait GoogleScope(val value: String)

  private object DriveMetadataReadonly extends GoogleScope("https://www.googleapis.com/auth/drive.metadata.readonly")

  private object CalendarReadonly extends GoogleScope("https://www.googleapis.com/auth/calendar.readonly")

  private sealed trait GoogleAccessType(val value: String)

  private object Online extends GoogleAccessType("online")

  private object Offline extends GoogleAccessType("offline")

  private sealed trait ResponseType(val value: String)

  private object Code extends ResponseType("code")


  def get(context: AppContext[IO]) ={

    val scheme = Uri.Scheme.https
    val domain = "accounts.google.com"
    val path = "o/oauth2/v2/auth".split("/").map(PathElm.apply).toList

//    val scope = s"${DriveMetadataReadonly.value} ${CalendarReadonly.value}"
    val scope = DriveMetadataReadonly.value
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
    } yield url
  }
}


class GoogleAuthController(context: AppContext[IO]) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO]{

    case request @ GET -> Root / "redirect" => for{
      url <- GoogleAuthRedirectLink.get(context)
      response <- Ok(url.toString)
    } yield response

    case request @ GET -> Root / "callback" =>{
      case class GoogleCallbackParams(
                                     state:String,
                                     iss:String,
                                     code:String,
                                     scope:String,
                                     )
      for {
        params <- request.as[GoogleCallbackParams]
        _ <- context.logger.info(params.code)
        response <- Ok("callback")
      } yield response
    }
  }
}
