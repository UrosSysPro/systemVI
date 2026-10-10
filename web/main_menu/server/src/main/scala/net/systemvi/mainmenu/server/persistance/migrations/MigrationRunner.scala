package net.systemvi.mainmenu.server.persistance.migrations

import cats.*
import cats.implicits.*
import cats.effect.*
import doobie.*
import doobie.implicits.*

/**
 * Applies and reverts migrations, remembering what has already run.
 *
 * The applied migrations are tracked by name in the `SchemaMigrations` table.
 * Ordering is by insertion, so `down` always reverts the most recently applied
 * migration, and calling it repeatedly walks back through history.
 */
object MigrationRunner {

  /** Runs every migration that has not been applied yet, in registry order. */
  def up[F[_]: MonadCancelThrow](xa: Transactor[F]): F[List[String]] =
    for {
      _ <- ensureTable(xa)
      applied <- recorded(xa)
      pending = Migrations.all(xa).filterNot(migration => applied.contains(migration.name))
      ran <- pending.traverse(migration => migration.up *> record(xa, migration.name).as(migration.name))
    } yield ran

  /** Reverts the most recently applied migration, if any. */
  def down[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Option[String]] =
    for {
      _ <- ensureTable(xa)
      latest <- latestApplied(xa)
      reverted <- latest.traverse { name =>
        Migrations.all(xa).find(_.name == name) match {
          case Some(migration) => migration.down *> unrecord(xa, name).as(name)
          case None =>
            MonadCancelThrow[F].raiseError(
              new IllegalStateException(s"Migration '$name' was applied but is not in the registry")
            )
        }
      }
    } yield reverted

  private def ensureTable[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Int] =
    sql"""
      create table if not exists SchemaMigrations(
        id integer primary key autoincrement,
        name varchar(255) not null unique,
        runAt timestamp not null default current_timestamp
      )
    """.update.run.transact(xa)

  private def recorded[F[_]: MonadCancelThrow](xa: Transactor[F]): F[List[String]] =
    sql"select name from SchemaMigrations order by id".query[String].to[List].transact(xa)

  private def latestApplied[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Option[String]] =
    sql"select name from SchemaMigrations order by id desc limit 1".query[String].option.transact(xa)

  private def record[F[_]: MonadCancelThrow](xa: Transactor[F], name: String): F[Int] =
    sql"insert into SchemaMigrations(name) values ($name)".update.run.transact(xa)

  private def unrecord[F[_]: MonadCancelThrow](xa: Transactor[F], name: String): F[Int] =
    sql"delete from SchemaMigrations where name = $name".update.run.transact(xa)
}
