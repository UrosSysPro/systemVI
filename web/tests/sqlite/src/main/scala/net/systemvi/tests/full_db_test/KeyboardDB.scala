package net.systemvi.tests.full_db_test

import cats.effect.kernel.MonadCancelThrow
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*

import java.util.UUID

trait KeyboardDB[F[_]] {
  def dropTable():F[Int]
  def createTable():F[Int]

  def add(keyboard: Keyboard):F[Int]

  def get(uuid:UUID):F[Option[Keyboard]]
  def get():F[List[Keyboard]]
  def getDto():F[List[KeyboardDto]]
}

object KeyboardDB {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]):KeyboardDB[F] = {
    new KeyboardDB[F] {
      import SqlMappings.given

      override def dropTable(): F[Int] =
        sql"""drop table if exists Keyboards""".stripMargin('|').update.run.transact(xa)

      override def createTable(): F[Int] =
        sql"""create table if not exists Keyboards(
             |  uuid UUID,
             |  name varchar(255),
             |  switchId UUID
             |)""".stripMargin('|').update.run.transact(xa)

      override def add(keyboard: Keyboard): F[Int] =
        sql"""
          |insert into Keyboards(uuid,name,switchId)
          |values(${keyboard.uuid.toString},${keyboard.name},${keyboard.switchId.toString})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Keyboard]] =
        sql"""
          | select *
          | from Keyboards
          | where uuid = ${uuid.toString}
           """.stripMargin('|').query[Keyboard].option.transact(xa)

      override def get(): F[List[Keyboard]] =
        sql"""
             | select *
             | from Keyboards
           """.stripMargin('|').query[Keyboard].to[List].transact(xa)

      override def getDto(): F[List[KeyboardDto]] =
        sql"""
          |select keyboard.uuid,keyboard.name,switch.uuid,switch.name,switch.type,manufacturer.uuid,manufacturer.name
          |from Keyboards as keyboard
          |join Switches as switch on keyboard.switchId = switch.uuid
          |join Manufacturers as manufacturer on switch.manufacturerId = manufacturer.uuid
           """.stripMargin('|').query[KeyboardDto].to[List].transact(xa)
    }
  }
}
