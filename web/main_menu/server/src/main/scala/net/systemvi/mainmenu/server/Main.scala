package net.systemvi.mainmenu.server

import cats.effect.*
import com.comcast.ip4s.{Host, Port}
import net.systemvi.mainmenu.server.api.routes.router
import net.systemvi.mainmenu.server.config.Config
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.persistance.migrations.MigrationRunner
import net.systemvi.mainmenu.server.persistance.seeders.Seeders
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger

def server(using context: AppContext[IO]) = EmberServerBuilder
  .default[IO]
  .withHost(context.config.server.host)
  .withPort(context.config.server.port)
  .withLogger(Slf4jLogger.getLogger[IO])
  .withHttpApp(router.orNotFound)
  .build

object Main extends IOApp {

  val serverApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    given AppContext[IO] = context
    for {
      _ <- server.use(_ => IO.never)
    } yield ExitCode.Success
  }

  val migrationApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    for {
      applied <- MigrationRunner.up(context.xa)
      _ <- IO.println(
        if (applied.isEmpty) "Nothing to migrate."
        else s"Applied ${applied.size} migration(s): ${applied.mkString(", ")}"
      )
    } yield ExitCode.Success
  }

  val migrationDownApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    for {
      reverted <- MigrationRunner.down(context.xa)
      _ <- IO.println(reverted.fold("Nothing to roll back.")(name => s"Rolled back: $name"))
    } yield ExitCode.Success
  }

  val seedApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    for {
      _ <- Seeders.seed(context.xa)
    } yield ExitCode.Success
  }

  val configApp: IO[ExitCode] = {
    val logger = Slf4jLogger.getLogger[IO]
    for {
      config <- Config.instance.load[IO]
      _ <- logger.info(config.server.toString)
    } yield ExitCode.Success
  }

  override def run(args: List[String]): IO[ExitCode] = {
    args match {
      case _ if args.isEmpty => serverApp
      case _ if args.head == "migrate" && args.tail.headOption.contains("down") => migrationDownApp
      case _ if args.head == "migrate" => migrationApp
      case _ if args.head == "seed" => seedApp
      case _ if args.head == "config" => configApp
      case _ => IO.println("usage: run [migrate|migrate down|seed|config]").as(ExitCode.Success)
    }
  }
}
