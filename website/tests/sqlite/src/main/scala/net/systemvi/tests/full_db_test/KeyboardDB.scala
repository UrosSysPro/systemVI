package net.systemvi.tests.full_db_test

import cats.effect.kernel.MonadCancelThrow
import doobie.*

import java.util.UUID

trait KeyboardDB[F[_]] {
  def dropTable():F[Int]
  def createTable():F[Int]

  def add(keyboard: Keyboard):F[Int]

  def get(uuid:UUID):F[Option[Keyboard]]
  def get():F[List[Keyboard]]
}

object KeyboardDB {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]):KeyboardDB[F] = {
    new KeyboardDB[F] {
      override def dropTable(): F[Int] = ???

      override def createTable(): F[Int] = ???

      override def add(keyboard: Keyboard): F[Int] = ???

      override def get(uuid: UUID): F[Option[Keyboard]] = ???

      override def get(): F[List[Keyboard]] = ???
    }
  }
}
