package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*

class __04_alter_items_add_categoryId_column[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] = addColumn("Items", "categoryId", "UUID null")

  override def down: F[Int] = dropColumn("Items", "categoryId")
}
