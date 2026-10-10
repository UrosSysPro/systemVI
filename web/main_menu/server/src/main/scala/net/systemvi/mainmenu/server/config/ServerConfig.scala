package net.systemvi.mainmenu.server.config

import cats.effect.*
import com.comcast.ip4s.{Host, Port}
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

  given Decoder[ServerConfig] = Decoder.instance { cursor =>
    for {
      host <- cursor.get[String]("host")
        .map(Host.fromString)
        .map(_.getOrElse(throw Exception("invalid host")))
      port <- cursor.get[String]("port")
        .map(Port.fromString)
        .map(_.getOrElse(throw Exception("invalid port")))
      serverUrl <- cursor.get[String]("serverUrl")
        .map(Uri.fromString)
        .map(_.getOrElse(throw Exception("invalid serverUrl")))
      clientUrl <- cursor.get[String]("clientUrl")
        .map(Uri.fromString)
        .map(_.getOrElse(throw Exception("invalid clientUrl")))
    } yield ServerConfig(host, port, serverUrl, clientUrl)
  }

  given ConfigDecoder[String, ServerConfig] = circeConfigDecoder("ServerConfigDecoder")

  def instance: ConfigValue[Effect, ServerConfig] =
    file(Paths.get("config/server.json")).as[ServerConfig]
}
