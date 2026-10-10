package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor

case class DatabaseContext[F[_]](
  menus: MenuContext[F],
  items: ItemContext[F],
  categories: CategoryContext[F],
)

object DatabaseContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): DatabaseContext[F] =
    DatabaseContext(
      MenuContext.create(xa),
      ItemContext.create(xa),
      CategoryContext.create(xa),
    )
}
