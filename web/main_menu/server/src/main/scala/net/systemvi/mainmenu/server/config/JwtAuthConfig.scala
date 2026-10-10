package net.systemvi.mainmenu.server.config

import ciris.*
import ciris.circe.circeConfigDecoder
import io.circe.*

import java.nio.file.Paths

/** HMAC secret used to sign and verify session JWTs, read from `config/jwt-auth.json`. */
case class JwtAuthConfig(secret: String)

object JwtAuthConfig {
  given Decoder[JwtAuthConfig] = Decoder.instance(_.get[String]("secret").map(JwtAuthConfig.apply))

  given ConfigDecoder[String, JwtAuthConfig] = circeConfigDecoder("JwtAuthConfigDecoder")

  def instance: ConfigValue[Effect, JwtAuthConfig] =
    file(Paths.get("config/jwt-auth.json")).as[JwtAuthConfig]
}
