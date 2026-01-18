package net.systemvi.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object EntitySpecificationMigration {
  def createTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |create table if not exists EntitySpecifications(
         |  entityUUID UUID,
         |  key varchar(255),
         |  value varchar(255),
         |  "order" int
         |)
         |""".stripMargin('|').update.run.transact(xa)

  def dropTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |drop table if exists EntitySpecifications
         |""".stripMargin('|').update.run.transact(xa)
}
