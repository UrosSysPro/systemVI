package net.systemvi.mainmenu.server.config

import cats.implicits.*
import cats.effect.*
import ciris.*

case class Config(
  server: ServerConfig,
  googleAuth: GoogleAuthConfig,
  jwtAuth: JwtAuthConfig,
)

object Config {
  def instance: ConfigValue[Effect, Config] =
    (ServerConfig.instance, GoogleAuthConfig.instance, JwtAuthConfig.instance).parMapN(Config.apply)
}
