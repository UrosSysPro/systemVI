package net.systemvi.server.config

import cats.effect.IO
import io.circe.*
import ciris.*
import ciris.circe.circeConfigDecoder

import java.nio.file.Paths

case class ServerConfig(
                             host: String,
                             port: String,
                           )

object ServerConfig {

  given Decoder[ServerConfig] = Decoder.instance{ cursor => for{
    host <- cursor.get[String]("host")
    port <- cursor.get[String]("port")
  } yield ServerConfig(host,port) }

  given ConfigDecoder[String,ServerConfig] = circeConfigDecoder("ServerConfigDecoder")

  def instance: ConfigValue[Effect, ServerConfig] = file(Paths.get("../server.json")).as[ServerConfig]
}
