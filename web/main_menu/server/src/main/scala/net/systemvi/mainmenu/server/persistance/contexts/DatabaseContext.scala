package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor

case class DatabaseContext[F[_]](
  items: ItemContext[F],
  categories: CategoryContext[F],
  restaurants: RestaurantContext[F],
  restaurantTables: RestaurantTableContext[F],
  roles: RoleContext[F],
  authIdentities: AuthIdentityContext[F],
  users: UserContext[F],
)

object DatabaseContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): DatabaseContext[F] =
    DatabaseContext(
      ItemContext.create(xa),
      CategoryContext.create(xa),
      RestaurantContext.create(xa),
      RestaurantTableContext.create(xa),
      RoleContext.create(xa),
      AuthIdentityContext.create(xa),
      UserContext.create(xa),
    )
}
