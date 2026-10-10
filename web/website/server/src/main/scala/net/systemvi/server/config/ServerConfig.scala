package net.systemvi.server.config

import cats.effect.*
import com.comcast.ip4s.{ipv4, port, Host, Port}
import io.circe.*
import ciris.*
import ciris.circe.circeConfigDecoder
import org.http4s.*

import java.nio.file.Paths

case class ServerConfig(
                             host: Host,
                             port: Port,
                             serverUrl: Uri,
                             clientUrl: Uri,
                           )

object ServerConfig {

  given Decoder[ServerConfig] = Decoder.instance{ cursor => for{
    host <- cursor.get[String]("host")
      .map{Host.fromString}
      .map{_.getOrElse(throw Exception())}
    port <- cursor.get[String]("port")
      .map{Port.fromString}
      .map{_.getOrElse(throw Exception())}
    serverUrl <- cursor.get[String]("serverUrl")
      .map{Uri.fromString}
      .map{_.getOrElse(throw Exception())}
    clientUrl <- cursor.get[String]("clientUrl")
      .map{Uri.fromString}
      .map{_.getOrElse(throw Exception())}
  } yield ServerConfig(
    host,
    port,
    serverUrl,
    clientUrl
  ) }

  given ConfigDecoder[String,ServerConfig] = circeConfigDecoder("ServerConfigDecoder")

  def instance: ConfigValue[Effect, ServerConfig] = file(Paths.get("config/server.json")).as[ServerConfig]
}
