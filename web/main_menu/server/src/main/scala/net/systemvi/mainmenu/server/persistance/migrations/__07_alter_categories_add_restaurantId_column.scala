package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*

class __07_alter_categories_add_restaurantId_column[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] = addColumn("Categories", "restaurantId", "UUID null")

  override def down: F[Int] = dropColumn("Categories", "restaurantId")
}
