package net.systemvi.server.config

import cats.*
import cats.implicits.*

import cats.effect.*
import cats.effect.implicits.*

import ciris.*

case class Config(
                 server: ServerConfig,
                 googleAuthConfig: GoogleAuthConfig,
                 jwtAuthConfig: JwtAuthConfig,
                 )
object Config {
  def instance: ConfigValue[Effect, Config] = (
    ServerConfig.instance,
    GoogleAuthConfig.instance,
    JwtAuthConfig.instance,
  ).parMapN(Config.apply)
}
