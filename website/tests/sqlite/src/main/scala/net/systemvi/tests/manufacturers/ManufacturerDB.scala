package net.systemvi.tests.manufacturers

import java.util.UUID

import cats.*
import cats.implicits.*
import cats.data.*
import cats.effect.*
import cats.effect.implicits.*

import doobie.{*,given}
import doobie.implicits.{*,given}
import doobie.generic.auto.{*,given}
import doobie.util.Read.{*,given}
import doobie.util.transactor.Transactor.{*,given}

object ManufacturerSQL {
  given read:Read[Manufacturer] = Read[(UUID,String)].map{
    case (uuid,name)=>Manufacturer(uuid,name)
  }

  given write:Write[Manufacturer] = Write[(UUID,String)].contramap{ m=>
    (m.uuid,m.name)
  }
}

trait ManufacturerDB[F[_]] {
  def createTable():F[Int]
  def add(manufacturer: Manufacturer):F[Int]
  def get(uuid:UUID):F[Option[Manufacturer]]
  def get():F[List[Manufacturer]]
}

object ManufacturerDB {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): ManufacturerDB[F] = {
    new ManufacturerDB[F] {

      import ManufacturerSQL.given

      override def createTable(): F[Int] =
        sql"""
             |create table if not exists Manufacturers(
             |  uuid UUID,
             |  name varchar(255)
             |)
             |"""
          .stripMargin('|').update.run.transact(xa)

      override def add(manufacturer: Manufacturer): F[Int] =
        sql"""
             |insert into Manufacturers(uuid,name)
             |values(${manufacturer.uuid.toString},${manufacturer.name})
             |"""
          .stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Manufacturer]] =
        sql"""
             |select *
             |from Manufacturers
             |where uuid = ${uuid.toString}
             |"""
          .stripMargin('|').query[Manufacturer].option.transact(xa)

      override def get(): F[List[Manufacturer]] =
        sql"""
             |select name
             |from Manufacturers
             |"""
          .stripMargin('|').query[Manufacturer].to[List].transact(xa)
    }
  }
}
