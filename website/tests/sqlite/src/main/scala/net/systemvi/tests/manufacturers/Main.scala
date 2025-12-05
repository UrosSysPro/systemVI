package net.systemvi.tests.manufacturers

import cats.effect.{IO, IOApp}

import java.util.UUID
import cats.data.NonEmptyList
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import java.util.UUID
import doobie.util.transactor.Transactor.*

case class Manufacturer(uuid:UUID,name:String)

trait ManufacturerDB[F[_]] {
  def createTable():F[Unit]
  def add(manufacturer: Manufacturer):F[Unit]
  def get(uuid:UUID):F[Option[Manufacturer]]
  def get():F[List[Manufacturer]]
}

object ManufacturerDB {
  def create(xa:Transactor[IO]) = {
    new ManufacturerDB[IO] {

      override def createTable(): IO[Unit] = ???

      override def add(manufacturer: Manufacturer): IO[Unit] = ???

      override def get(uuid: UUID): IO[Option[Manufacturer]] = ???

      override def get(): IO[List[Manufacturer]] = ???
    }
  }
}

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    _ <- IO.println("hello")
  } yield {}
}
