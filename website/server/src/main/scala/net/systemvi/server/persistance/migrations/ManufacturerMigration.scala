package net.systemvi.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object ManufacturerMigration {
  def createTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |create table if not exists Manufacturers(
         |  uuid UUID,
         |  name varchar(255)
         |)
         |""".stripMargin('|').update.run.transact(xa)

  def dropTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |drop table if exists Manufacturers
         |""".stripMargin('|').update.run.transact(xa)
}
