package net.systemvi.tests.full_db_test

import cats.effect.kernel.MonadCancelThrow
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*

import java.util.UUID

trait SwitchDB[F[_]] {
  def dropTable():F[Int]
  def createTable():F[Int]

  def add(switch: Switch):F[Int]

  def get(uuid:UUID):F[Option[Switch]]
  def get():F[List[Switch]]
}

object SwitchDB {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]):SwitchDB[F] = {
    new SwitchDB[F] {
      import SqlMappings.given
      override def dropTable(): F[Int] = sql"drop table if exists Switches".update.run.transact(xa)

      override def createTable(): F[Int] =
        sql"""
             |create table Switches(
             | uuid UUID,
             | name varchar(255),
             | manufacturerId UUID,
             | type int
             |)
           """.stripMargin('|').update.run.transact(xa)

      override def add(s: Switch): F[Int] =
        sql"""
             |insert into Switches(uuid,name,manufacturerId,type)
             |values (${s.uuid.toString},${s.name},${s.manufacturerId.toString},${s.switchType.value})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Switch]] =
        sql"""
             |select *
             |from Switches
             |where uuid = ${uuid.toString}
           """.stripMargin('|').query[Switch].option.transact(xa)

      override def get(): F[List[Switch]] =
        sql"""
             |select *
             |from Switches
           """.stripMargin('|').query[Switch].to[List].transact(xa)
    }
  }
}
