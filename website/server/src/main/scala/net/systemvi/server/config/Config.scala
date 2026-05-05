package net.systemvi.server.config

import cats.*
import cats.implicits.*

import cats.effect.*
import cats.effect.implicits.*

import ciris.*

case class Config(
                 server: ServerConfig,
                 googleAuthConfig: GoogleAuthConfig,
                 )
object Config {
  def instance: ConfigValue[Effect, Config] = (
    ServerConfig.instance,
    GoogleAuthConfig.instance,
  ).parMapN(Config.apply)
}
