package net.systemvi.server.config

import io.circe.*
import ciris.*
import ciris.circe.circeConfigDecoder

import java.nio.file.Paths

case class JwtAuthConfig(
                         secret: String,
                       )

object JwtAuthConfig {

  given Decoder[JwtAuthConfig] = Decoder.instance{ cursor => for{
    secret <- cursor.get[String]("secret")
  } yield JwtAuthConfig(secret) }

  given ConfigDecoder[String,JwtAuthConfig] = circeConfigDecoder("JwtAuthConfigDecoder")

  def instance: ConfigValue[Effect, JwtAuthConfig] = file(Paths.get("../jwt-auth.json")).as[JwtAuthConfig]
}
