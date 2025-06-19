package net.systemvi.server
import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.dsl.io.*
import org.http4s.server.Router
import org.http4s.server.staticcontent._
import net.systemvi.server.api.*
import net.systemvi.server.website.*
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger

val httpApp:HttpApp[IO]=Router(
  "/api"->apiService,                                         //for api
  "/"->websiteService
//  "/"->fileService[IO](FileService.Config("./public/dist/index.html")),       //for static files
//  "/"->fileService[IO](FileService.Config("./public/dist")),  //for website
//  "/"->fileService[IO](FileService.Config("./public")),       //for static files
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