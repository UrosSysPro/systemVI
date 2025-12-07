package net.systemvi.tests.manufacturers

import java.util.UUID

import cats.*
import cats.implicits.*
import cats.data.*
import cats.effect.*
import cats.effect.implicits.*

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.util.transactor.Transactor.*


trait ManufacturerDB[F[_]] {
  def createTable():F[Unit]
  def add(manufacturer: Manufacturer):F[Unit]
  def get(uuid:UUID):F[Option[Manufacturer]]
  def get():F[List[Manufacturer]]
}

object ManufacturerDB {
  def create(xa:Transactor[IO]) = {
    new ManufacturerDB[IO] {
      override def createTable(): IO[Unit] = sql"create table if not exists Manufacturers(uuid UUID, name varchar(255))".update.run.transact(xa).void

      override def add(manufacturer: Manufacturer): IO[Unit] = sql"insert into Manufacturers(uuid,name) values(${manufacturer.uuid.toString},${manufacturer.name})".update.run.transact(xa).void

      override def get(uuid: UUID): IO[Option[Manufacturer]] = ???

//      override def get(): IO[List[Manufacturer]] = sql"select name from Manufacturers".query[Manufacturer].to[List].transact(xa)
      override def get(): IO[List[Manufacturer]] = ???
    }
  }
}
