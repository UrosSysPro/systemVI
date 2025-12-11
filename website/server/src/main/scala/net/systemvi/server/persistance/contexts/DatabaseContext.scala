package net.systemvi.server.persistance.contexts

import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor

case class DatabaseContext[F[_]](
                            manufacturers:ManufacturerContext[F],
                          )


object DatabaseContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): DatabaseContext[F] = {
    DatabaseContext(
      ManufacturerContext.create[F](xa)
    )
  }
}