package net.systemvi.server.config

import cats.effect.IO
import io.circe.*
import ciris.*
import ciris.circe.circeConfigDecoder

import java.nio.file.Paths

case class ServerConfig(
                             host: String,
                             port: String,
                             serverUrl: String,
                             clientUrl: String,
                           )

object ServerConfig {

  given Decoder[ServerConfig] = Decoder.instance{ cursor => for{
    host <- cursor.get[String]("host")
    port <- cursor.get[String]("port")
    serverUrl <- cursor.get[String]("serverUrl")
    clientUrl <- cursor.get[String]("clientUrl")
  } yield ServerConfig(host,port,serverUrl,clientUrl) }

  given ConfigDecoder[String,ServerConfig] = circeConfigDecoder("ServerConfigDecoder")

  def instance: ConfigValue[Effect, ServerConfig] = file(Paths.get("config/server.json")).as[ServerConfig]
}
