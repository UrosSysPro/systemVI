package net.systemvi.mainmenu.server.config

import cats.*
import cats.implicits.*
import cats.effect.*
import ciris.*

case class Config(server: ServerConfig)

object Config {
  def instance: ConfigValue[Effect, Config] =
    ServerConfig.instance.map(Config.apply)
}
