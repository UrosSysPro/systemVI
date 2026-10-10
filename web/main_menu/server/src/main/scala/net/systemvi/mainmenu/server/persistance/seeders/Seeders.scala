package net.systemvi.mainmenu.server.persistance.seeders

import cats.*
import cats.implicits.*
import cats.effect.*
import doobie.util.transactor.Transactor

object Seeders {
  def seed[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Unit] = for {
    _ <- RestaurantSeeders.seed(xa)
  } yield ()
}
