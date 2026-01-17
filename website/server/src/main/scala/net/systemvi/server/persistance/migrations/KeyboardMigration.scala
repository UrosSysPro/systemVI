package net.systemvi.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*

object KeyboardMigration {
  def createTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |create table if not exists Keyboards(
         |  uuid UUID,
         |  switchUUID UUID,
         |  profileId int,
         |  name varchar(255),
         |  codeName varchar(255)
         |)
         |""".stripMargin('|').update.run.transact(xa)

  def dropTable[F[_]:MonadCancelThrow](xa:Transactor[F]):F[Int] =
    sql"""
         |drop table if exists Keyboards
         |""".stripMargin('|').update.run.transact(xa)
}
