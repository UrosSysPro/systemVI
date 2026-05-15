package net.systemvi.server.persistance.migrations

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object Migrations {
  def migrate[F[_]: MonadCancelThrow: Sync: Async](xa:Transactor[F]): F[Unit] = for{
    _ <- ManufacturerMigration.createTable(xa)
    _ <- SwitchMigration.createTable(xa)
    _ <- KeyboardMigration.createTable(xa)
    _ <- EntityImageMigration.createTable(xa)
    _ <- EntitySpecificationMigration.createTable(xa)
    _ <- FilamentMigration.createTable(xa)
    _ <- ApplicationMigration.createTable(xa)
  }yield()

  def dropAll[F[_]: MonadCancelThrow: Sync: Async](xa:Transactor[F]): F[Unit] = for{
    _ <- ManufacturerMigration.dropTable(xa)
    _ <- SwitchMigration.dropTable(xa)
    _ <- KeyboardMigration.dropTable(xa)
    _ <- EntityImageMigration.dropTable(xa)
    _ <- EntitySpecificationMigration.dropTable(xa)
    _ <- FilamentMigration.dropTable(xa)
    _ <- ApplicationMigration.dropTable(xa)
  }yield()
}
