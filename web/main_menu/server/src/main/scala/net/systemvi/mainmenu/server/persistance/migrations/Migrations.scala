package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*

/**
 * Registry of every migration, in the order in which they must be applied.
 *
 * To add a migration: create a new `__NN_...` class in this package and append
 * it here. The order in this list is what [[MigrationRunner]] executes.
 */
object Migrations {
  def all[F[_]: MonadCancelThrow](xa: Transactor[F]): List[BaseMigration[F]] = List(
    __01_create_menus_table[F](xa),
    __02_create_items_table[F](xa),
    __03_create_categories_table[F](xa),
    __04_alter_items_add_categoryId_column[F](xa),
  )
}
