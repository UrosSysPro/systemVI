package net.systemvi.server.persistance.contexts

import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor

case class DatabaseContext[F[_]](
                            manufacturers: ManufacturerContext[F],
                            switches: SwitchContext[F],
                            keyboards: KeyboardContext[F],
                            entityImages: EntityImageContext[F],
                            entitySpecifications: EntitySpecificationContext[F],
                          )


object DatabaseContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): DatabaseContext[F] = {
    DatabaseContext(
      ManufacturerContext.create(xa),
      SwitchContext.create(xa),
      KeyboardContext.create(xa),
      EntityImageContext.create(xa),
      EntitySpecificationContext.create(xa),
    )
  }
}