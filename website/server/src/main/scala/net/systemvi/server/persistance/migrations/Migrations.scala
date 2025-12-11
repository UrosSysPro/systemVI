package net.systemvi.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object Migrations {
  def migrate(xa:Transactor[IO]): IO[Unit] = for{
    _ <- ManufacturerMigration.createTable(xa)
  }yield()

  def dropAll(xa:Transactor[IO]): IO[Unit] = for{
    _ <- ManufacturerMigration.dropTable(xa)
  }yield()
}
