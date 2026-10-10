package net.systemvi.mainmenu.server.persistance.database

import cats.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.hikari.*

import java.nio.file.{Files, Paths}

/** Location of the SQLite database file, relative to the process working directory. */
val databasePath: String = "db/main_menu.db"

val databaseUrl: String = s"jdbc:sqlite:$databasePath"

/**
 * Deletes the SQLite database file along with its sidecar files.
 *
 * Only safe when no connection is open (e.g. from the CLI before a transactor is created);
 * otherwise the file will be locked on Windows.
 */
def deleteDatabase[F[_]: Sync](path: String = databasePath): F[Unit] =
  Sync[F].blocking {
    List(path, s"$path-wal", s"$path-shm", s"$path-journal")
      .foreach(file => Files.deleteIfExists(Paths.get(file)))
  }

def sqlite[F[_]: Async: MonadCancelThrow]: Resource[F, Transactor[F]] = for {
  ec <- ExecutionContexts.fixedThreadPool[F](32)
  xa <- HikariTransactor.newHikariTransactor[F](
    "org.sqlite.JDBC",
    databaseUrl,
    "",
    "",
    ec
  )
} yield xa
