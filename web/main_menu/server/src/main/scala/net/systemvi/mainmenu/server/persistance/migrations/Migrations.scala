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
    __05_create_restaurants_table[F](xa),
    __06_create_restaurant_tables_table[F](xa),
    __07_alter_categories_add_restaurantId_column[F](xa),
    __08_drop_menus_table[F](xa),
    __09_create_roles_table[F](xa),
    __10_create_auth_identities_table[F](xa),
    __11_create_users_table[F](xa),
    __12_alter_categories_and_items_add_imageBlob_column[F](xa),
  )
}
