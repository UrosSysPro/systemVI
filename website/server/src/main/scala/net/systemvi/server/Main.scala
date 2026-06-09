package net.systemvi.server

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import com.comcast.ip4s.{Host, Port}
import fs2.io.net.Network
import net.systemvi.server.api.*
import net.systemvi.server.api.routes.*
import net.systemvi.server.config.Config
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.persistance.database.*
import net.systemvi.server.persistance.migrations.Migrations
import net.systemvi.server.persistance.seeders.Seeders
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger

def server(using context:AppContext[IO]) = EmberServerBuilder
  .default[IO]
  .withHost(context.config.server.host)
  .withPort(context.config.server.port)
  .withLogger(Slf4jLogger.getLogger[IO])
  .withHttpApp(router)
  .build

object Main extends IOApp{

  val serverApp:IO[ExitCode] = AppContext.create[IO].use { context =>
    given AppContext[IO] = context
    for {
      _ <- server.use(_ => IO.never)
    } yield ExitCode.Success
  }

  val migrationApp:IO[ExitCode] = AppContext.create[IO].use { context => for {
    _ <- Migrations.dropAll(context.xa)
    _ <- Migrations.migrate(context.xa)
  } yield ExitCode.Success }

  val seedApp:IO[ExitCode] = AppContext.create[IO].use{ context => for{
    _ <- Seeders.seed(context.xa)
  }yield ExitCode.Success}

  val configApp:IO[ExitCode] = {
    val logger = Slf4jLogger.getLogger[IO]

    for{
      config <- Config.instance.load[IO]
      _ <- logger.info(config.googleAuthConfig.clientId)
      _ <- logger.info(config.googleAuthConfig.clientSecret.valueShortHash)
    }yield ExitCode.Success
  }

  override def run(args: List[String]): IO[ExitCode] = {
    args match {
      case _ if args.isEmpty => serverApp
      case _ if args.head == "migrate" => migrationApp
      case _ if args.head == "seed" => seedApp
      case _ if args.head == "config" => configApp
      case _ => IO.println("wrong input").map(_ => ExitCode.Success)
    }
  }
}