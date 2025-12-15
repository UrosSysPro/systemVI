package net.systemvi.server

import cats.*
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.server.Router
import net.systemvi.server.api.*
import net.systemvi.server.api.controllers.manufacturerRoutes
import net.systemvi.server.persistance.contexts.ApplicationContext
import net.systemvi.server.website.*
import org.http4s.ember.server.EmberServerBuilder
import org.typelevel.log4cats.slf4j.Slf4jLogger
import net.systemvi.server.persistance.database.*
import net.systemvi.server.persistance.migrations.Migrations

def httpApp(context: ApplicationContext[IO]):HttpApp[IO] = {
  given ApplicationContext[IO] = context
  Router(
    "/api"->apiService,
    "/api/manufacturers"->manufacturerRoutes,
    "/"->websiteService
  ).orNotFound
}

def server(context:ApplicationContext[IO])=EmberServerBuilder
  .default[IO]
  .withHost(ipv4"0.0.0.0")
  .withPort(port"8080")
  .withLogger(Slf4jLogger.getLogger[IO])
  .withHttpApp(httpApp(context))
  .build

object Main extends IOApp{
  val serverApp = sqlite.use{ xa =>
    val context = ApplicationContext.create(xa)
    for {
      _ <- server(context).use(_ => IO.never)
    } yield ExitCode.Success
  }

  val migrationApp = sqlite.use { xa => for {
    _ <- Migrations.dropAll(xa)
    _ <- Migrations.migrate(xa)
  } yield ExitCode.Success }

  val seedApp = for{
    _ <- IO.println("not implemented")
  }yield ExitCode.Success

  override def run(args: List[String]): IO[ExitCode] = {
    args match {
      case _ if args.isEmpty => serverApp
      case _ if args.head == "migrate" => migrationApp
      case _ if args.head == "seed" => migrationApp
      case _ => IO.println("wrong input").map(_=>ExitCode.Success)
    }
  }
}