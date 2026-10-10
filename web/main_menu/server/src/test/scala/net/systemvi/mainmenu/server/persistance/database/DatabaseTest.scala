package net.systemvi.mainmenu.server.persistance.database

import cats.effect.*
import munit.CatsEffectSuite

import java.nio.file.Files

class DatabaseTest extends CatsEffectSuite {

  test("deleteDatabase removes the database file and its sidecar files") {
    for {
      dir <- IO.blocking(Files.createTempDirectory("main-menu-db-"))
      path = dir.resolve("main_menu.db")
      _ <- IO.blocking {
        Files.writeString(path, "database")
        Files.writeString(dir.resolve("main_menu.db-wal"), "wal")
        Files.writeString(dir.resolve("main_menu.db-shm"), "shm")
        Files.writeString(dir.resolve("main_menu.db-journal"), "journal")
      }
      _ <- deleteDatabase[IO](path.toString)
      remaining <- IO.blocking(dir.toFile.list().length)
    } yield assertEquals(remaining, 0)
  }
}
