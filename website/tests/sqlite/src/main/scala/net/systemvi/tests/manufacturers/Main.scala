package net.systemvi.tests.manufacturers

import cats.effect.{IO, IOApp}

import java.util.UUID
import cats.data.NonEmptyList
import cats.effect.*
import cats.implicits.*
import doobie.{util, *}
import doobie.implicits.{*,given}
import doobie.generic.auto.{*,given}
import java.util.UUID
import doobie.util.transactor.Transactor.*

case class Manufacturer(uuid:UUID,name:String)

trait ManufacturerDB[F[_]] {
  def createTable():F[Unit]
  def add(manufacturer: Manufacturer):F[Unit]
  def get(uuid:UUID):F[Option[Manufacturer]]
  def get():F[List[String]]
}

object ManufacturerDB {
  def create(xa:Transactor[IO]) = IO.pure {
    new ManufacturerDB[IO] {

      override def createTable(): IO[Unit] = sql"create table if not exists Manufacturers(uuid UUID, name varchar(255))".update.run.transact(xa).void

      override def add(manufacturer: Manufacturer): IO[Unit] = sql"insert into Manufacturers(uuid,name) values(${manufacturer.uuid.toString},${manufacturer.name})".update.run.transact(xa).void

      override def get(uuid: UUID): IO[Option[Manufacturer]] = ???

      override def get(): IO[List[String]] = sql"select name from Manufacturers".query[String].to[List].transact(xa)
    }
  }
}

def sqlite() = IO{
  Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:test.db",
    None
  )
}

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    _   <- IO.println("creating db connection")
    xa  <- sqlite()
    db  <- ManufacturerDB.create(xa)
    _   <- db.createTable()
    _   <- List("Kailh","Gateron","Keychron")
            .map(name=>db.add(Manufacturer(UUID.randomUUID(),name)))
            .sequence
    manufacturers <- db.get()
    _   <- manufacturers
            .map(m=>IO.println(m))
            .sequence
  } yield {}
}
