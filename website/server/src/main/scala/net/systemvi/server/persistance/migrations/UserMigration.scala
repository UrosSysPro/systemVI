package net.systemvi.server.persistance.migrations

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import net.systemvi.server.persistance.contexts.*


class UserMigration[F[_]: MonadCancelThrow](xa: Transactor[F]) extends Migration[F] {
  override def up: F[Int] = {
    sql"""
      create table if not exists Users(
        uuid UUID primary key,
        email varchar(255) not null,
        name varchar(255),
        picture varchar(255) null
      );
    """.update.run.transact(xa)
  }

  override def down: F[Int] = {
    sql"""
      drop table if exists Users;
    """.update.run.transact(xa)
  }
}
