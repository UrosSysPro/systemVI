package net.systemvi.mainmenu.server

import cats.effect.*
import com.monovore.decline.*
import com.monovore.decline.effect.*
import net.systemvi.mainmenu.server.api.routes.router
import net.systemvi.mainmenu.server.config.Config
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import net.systemvi.mainmenu.server.persistance.database.deleteDatabase
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

object Main extends CommandIOApp(
  name = "main-menu",
  header = "Main Menu server: HTTP API, database migrations and seeding",
) {

  private val serverApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    given AppContext[IO] = context
    server.use(_ => IO.never).as(ExitCode.Success)
  }

  private val migrationApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    for {
      applied <- MigrationRunner.up(context.xa)
      _ <- IO.println(
        if (applied.isEmpty) "Nothing to migrate."
        else s"Applied ${applied.size} migration(s): ${applied.mkString(", ")}"
      )
    } yield ExitCode.Success
  }

  private val migrationForceApp: IO[ExitCode] =
    deleteDatabase[IO]().attempt.flatMap {
      case Right(_) =>
        for {
          _ <- IO.println("Deleted the database. Running every migration from scratch...")
          exitCode <- migrationApp
        } yield exitCode
      case Left(error) =>
        IO.println(
          s"Could not delete the database: ${error.getMessage}\n" +
            "Make sure the server is stopped first, since it keeps the database file open."
        ).as(ExitCode.Error)
    }

  private val migrationDownApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    for {
      reverted <- MigrationRunner.down(context.xa)
      _ <- IO.println(reverted.fold("Nothing to roll back.")(name => s"Rolled back: $name"))
    } yield ExitCode.Success
  }

  private val seedApp: IO[ExitCode] = AppContext.create[IO].use { context =>
    Seeders.seed(context.xa).as(ExitCode.Success)
  }

  private val configApp: IO[ExitCode] = {
    val logger = Slf4jLogger.getLogger[IO]
    for {
      config <- Config.instance.load[IO]
      _ <- logger.info(config.server.toString)
    } yield ExitCode.Success
  }

  private val serveCommand: Opts[IO[ExitCode]] =
    Opts.subcommand("serve", "Start the HTTP server") {
      Opts(serverApp)
    }

  private val migrateCommand: Opts[IO[ExitCode]] =
    Opts.subcommand("migrate", "Run pending migrations") {
      val down =
        Opts.subcommand("down", "Revert the most recently applied migration") {
          Opts(migrationDownApp)
        }
      val up =
        Opts
          .flag("force", "Delete the database and run every migration from scratch")
          .orFalse
          .map(force => if force then migrationForceApp else migrationApp)
      down.orElse(up)
    }

  private val seedCommand: Opts[IO[ExitCode]] =
    Opts.subcommand("seed", "Populate the database with default data") {
      Opts(seedApp)
    }

  private val configCommand: Opts[IO[ExitCode]] =
    Opts.subcommand("config", "Print the loaded configuration") {
      Opts(configApp)
    }

  override def main: Opts[IO[ExitCode]] =
    serveCommand
      .orElse(migrateCommand)
      .orElse(seedCommand)
      .orElse(configCommand)
      .orElse(Opts(serverApp)) // no arguments -> start the server
}
