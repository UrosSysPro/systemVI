package net.systemvi.tests.sqlite


import cats.data.NonEmptyList
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.util.UUID
import doobie.util.transactor.Transactor.*

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val xa:Transactor[IO] = Transactor.fromDriverManager[IO](
      "org.sqlite.JDBC",
      "jdbc:sqlite:test.db",
      None
    )

    for {
      _ <- IO.println("hello from sqlite test")
      version<-sql"select sqlite_version()"
        .query[String]
        .unique
        .transact(xa)
      _<-IO.println(s"Sqlite version: $version")
    } yield ()
  }
}
