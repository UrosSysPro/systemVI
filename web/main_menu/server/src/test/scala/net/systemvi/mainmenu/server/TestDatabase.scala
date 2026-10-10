package net.systemvi.mainmenu.server

import cats.effect.*
import doobie.*
import doobie.hikari.*
import net.systemvi.mainmenu.server.persistance.migrations.MigrationRunner

import java.nio.file.Files

object TestDatabase {

  /** A fresh SQLite database in a temp file, with no schema applied yet. */
  val empty: Resource[IO, Transactor[IO]] =
    for {
      ec <- ExecutionContexts.fixedThreadPool[IO](2)
      dbFile <- Resource.eval(IO.blocking(Files.createTempFile("main-menu-test-", ".db")))
      _ <- Resource.make(IO.unit)(_ => IO.blocking(Files.deleteIfExists(dbFile)).void)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.sqlite.JDBC",
        s"jdbc:sqlite:${dbFile.toAbsolutePath}",
        "",
        "",
        ec,
      )
    } yield xa

  /** A fresh SQLite database with every migration applied. */
  val transactor: Resource[IO, Transactor[IO]] =
    for {
      xa <- empty
      _ <- Resource.eval(MigrationRunner.up(xa))
    } yield xa
}
