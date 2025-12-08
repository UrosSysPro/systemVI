package net.systemvi.tests.full_db_test

import cats.effect.kernel.MonadCancelThrow
import doobie.*

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
      override def dropTable(): F[Int] = ???

      override def createTable(): F[Int] = ???

      override def add(switch: Switch): F[Int] = ???

      override def get(uuid: UUID): F[Option[Switch]] = ???

      override def get(): F[List[Switch]] = ???
    }
  }
}
