package net.systemvi.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object Migrations {
  def migrate(xa:Transactor[IO]): IO[Unit] = for{
    _ <- ManufacturerMigration.createTable(xa)
    _ <- SwitchMigration.createTable(xa)
    _ <- KeyboardMigration.createTable(xa)
    _ <- EntityImageMigration.createTable(xa)
    _ <- EntitySpecificationMigration.createTable(xa)
  }yield()

  def dropAll(xa:Transactor[IO]): IO[Unit] = for{
    _ <- ManufacturerMigration.dropTable(xa)
    _ <- SwitchMigration.dropTable(xa)
    _ <- KeyboardMigration.dropTable(xa)
    _ <- EntityImageMigration.dropTable(xa)
    _ <- EntitySpecificationMigration.dropTable(xa)
  }yield()
}
