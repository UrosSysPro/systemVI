package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

class __06_create_restaurant_tables_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists RestaurantTables(
          uuid UUID primary key,
          name varchar(255) not null,
          imageUrl varchar(255) not null,
          restaurantId UUID not null
        )
      """
    )

  override def down: F[Int] = dropTable("RestaurantTables")
}
