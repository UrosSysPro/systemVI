package net.systemvi.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object EntityImageMigration {
  def createTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |create table if not exists EntityImages(
         |  entityUUID UUID,
         |  imageUrl varchar(255),
         |  "order" int
         |)
         |""".stripMargin('|').update.run.transact(xa)

  def dropTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |drop table if exists EntityImages
         |""".stripMargin('|').update.run.transact(xa)
}
