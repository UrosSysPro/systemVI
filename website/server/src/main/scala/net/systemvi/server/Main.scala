package net.systemvi.server

import cats.*
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import net.systemvi.server.api.*
import net.systemvi.server.api.controllers.*
import net.systemvi.server.api.routes.*
import net.systemvi.server.persistance.contexts.AppContext
import net.systemvi.server.persistance.database.*
import net.systemvi.server.persistance.migrations.Migrations
import net.systemvi.server.persistance.seeders.Seeders
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jLogger

def server(using context:AppContext[IO])=EmberServerBuilder
  .default[IO]
  .withHost(ipv4"0.0.0.0")
  .withPort(port"8080")
  .withLogger(Slf4jLogger.getLogger[IO])
  .withHttpApp(router)
  .build

object Main extends IOApp{

  val serverApp:IO[ExitCode] = sqlite.use{ xa =>
    given AppContext[IO] = AppContext.create(xa)
    for {
      _ <- server.use(_ => IO.never)
    } yield ExitCode.Success
  }

  val migrationApp:IO[ExitCode] = sqlite.use { xa => for {
    _ <- Migrations.dropAll(xa)
    _ <- Migrations.migrate(xa)
  } yield ExitCode.Success }

  val seedApp:IO[ExitCode] = sqlite.use{xa=> for{
    _ <- Seeders.seed(xa)
  }yield ExitCode.Success}

  override def run(args: List[String]): IO[ExitCode] = {
    args match {
      case _ if args.isEmpty => serverApp
      case _ if args.head == "migrate" => migrationApp
      case _ if args.head == "seed" => seedApp
      case _ => IO.println("wrong input").map(_=>ExitCode.Success)
    }
  }
}