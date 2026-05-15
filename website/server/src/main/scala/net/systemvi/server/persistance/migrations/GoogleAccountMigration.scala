package net.systemvi.server.persistance.migrations

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import net.systemvi.server.persistance.contexts.*


class GoogleAccountMigration[F[_]: MonadCancelThrow](xa: Transactor[F]) extends Migration[F] {
  override def up: F[Int] = {
    sql"""
      create table if not exists GoogleAccounts(
        sub varchar(255) primary key ,
        email varchar(255) not null ,
        email_verified Boolean not null ,
        name varchar(255),
        picture varchar(255),
        given_name varchar(255),
        family_name varchar(255),
        locale varchar(255)
      );
    """.update.run.transact(xa)
  }

  override def down: F[Int] = {
    sql"""
      drop table if exists GoogleAccounts;
    """.update.run.transact(xa)
  }
}
