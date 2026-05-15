package net.systemvi.server.persistance.seeders

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*
import doobie.util.transactor.Transactor

object Seeders {
  def seed[F[_]: MonadCancelThrow](xa:Transactor[F]): F[Unit] = for{
    _ <- KeyboardSeeders.seed(xa)
    _ <- ApplicationSeeders.seed(xa)
  } yield ()
}
