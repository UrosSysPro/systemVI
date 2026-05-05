package net.systemvi.server.config


import cats.effect.IO
import io.circe.*
import ciris.*
import ciris.circe.circeConfigDecoder

import java.nio.file.Paths

case class GoogleAuthConfig(
                           clientId: String,
                           clientSecret: Secret[String],
                           )

object GoogleAuthConfig {

  given Decoder[GoogleAuthConfig] = Decoder.instance{ cursor => for{
    clientId <- cursor.downField("web").get[String]("client_id")
    clientSecret <- cursor.downField("web").get[String]("client_secret")
  } yield GoogleAuthConfig(clientId, Secret(clientSecret)) }

  given ConfigDecoder[String,GoogleAuthConfig] = circeConfigDecoder("GoogleAuthConfigDecoder")

  def instance: ConfigValue[Effect, GoogleAuthConfig] = file(Paths.get("../google-client-secret.json")).as[GoogleAuthConfig]
}
