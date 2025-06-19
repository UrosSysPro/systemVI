package net.systemvi.server

import cats.*
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router
import net.systemvi.server.api.*
import net.systemvi.server.website.*
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger

val httpApp:HttpApp[IO]=Router(
  "/api"->apiService,
  "/"->websiteService
).orNotFound

val server=EmberServerBuilder
  .default[IO]
  .withHost(ipv4"0.0.0.0")
  .withPort(port"8080")
  .withHttpApp(httpApp)
  .withLogger(Slf4jLogger.getLoggerFromName("app"))
  .build

object Main extends IOApp .Simple {
  override def run: IO[Unit] = server.use(_=>IO.never)
}