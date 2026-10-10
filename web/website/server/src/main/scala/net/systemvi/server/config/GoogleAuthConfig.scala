package net.systemvi.server.config


import cats.effect.IO
import io.circe.*
import ciris.*
import ciris.circe.circeConfigDecoder
import org.http4s.*

import java.nio.file.Paths

case class GoogleAuthConfig(
                           clientId: String,
                           clientSecret: Secret[String],
                           redirectUri: Uri,
                           callbackUri: Uri,
                           )

object GoogleAuthConfig {

  given Decoder[GoogleAuthConfig] = Decoder.instance{ cursor => for{
    clientId <- cursor.downField("web").get[String]("client_id")
    clientSecret <- cursor.downField("web").get[String]("client_secret")
    redirectUri <- cursor.downField("web").get[String]("redirect_uri")
    callbackUri <- cursor.downField("web").get[String]("callback_uri")
  } yield GoogleAuthConfig(
    clientId,
    Secret(clientSecret),
    Uri.fromString(redirectUri).getOrElse(throw Exception()),
    Uri.fromString(callbackUri).getOrElse(throw Exception()),
  ) }

  given ConfigDecoder[String,GoogleAuthConfig] = circeConfigDecoder("GoogleAuthConfigDecoder")

  def instance: ConfigValue[Effect, GoogleAuthConfig] = file(Paths.get("config/google-client-secret.json")).as[GoogleAuthConfig]
}
