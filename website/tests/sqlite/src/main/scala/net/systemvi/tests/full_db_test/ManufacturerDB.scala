package net.systemvi.tests.full_db_test

import cats.effect.kernel.MonadCancelThrow
import doobie.*

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
      override def dropTable(): F[Int] = ???

      override def createTable(): F[Int] = ???

      override def add(manufacturer: Manufacturer): F[Int] = ???

      override def get(uuid: UUID): F[Option[Manufacturer]] = ???

      override def get(): F[List[Manufacturer]] = ???
    }
  }
}
