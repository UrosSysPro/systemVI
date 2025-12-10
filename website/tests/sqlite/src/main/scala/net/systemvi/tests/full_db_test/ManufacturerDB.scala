package net.systemvi.tests.full_db_test

import cats.effect.kernel.MonadCancelThrow
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*


import java.util.UUID

trait ManufacturerDB[F[_]] {
  def dropTable():F[Int]
  def createTable():F[Int]
  
  def add(manufacturer: Manufacturer):F[Int]
  
  def get(uuid:UUID):F[Option[Manufacturer]]
  def get():F[List[Manufacturer]]
}

object ManufacturerDB {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]):ManufacturerDB[F] = {
    new ManufacturerDB[F] {
      import SqlMappings.given
      override def dropTable(): F[Int] = sql"drop table if exists Manufacturers".update.run.transact(xa)

      override def createTable(): F[Int] =
        sql"""
          |create table Manufacturers(
          | uuid UUID,
          | name varchar(255)
          |)
           """.stripMargin('|').update.run.transact(xa)

      override def add(manufacturer: Manufacturer): F[Int] =
        sql"""
          |insert into Manufacturers(uuid,name)
          |values (${manufacturer.uuid.toString},${manufacturer.name})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Manufacturer]] =
        sql"""
          |select *
          |from Manufacturers
          |where uuid = ${uuid.toString}
           """.stripMargin('|').query[Manufacturer].option.transact(xa)

      override def get(): F[List[Manufacturer]] =
        sql"""
             |select *
             |from Manufacturers
           """.stripMargin('|').query[Manufacturer].to[List].transact(xa)
    }
  }
}
