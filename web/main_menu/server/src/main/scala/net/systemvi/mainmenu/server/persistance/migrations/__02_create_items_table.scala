package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

class __02_create_items_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists Items(
          uuid UUID primary key,
          name varchar(255) not null,
          imageUrl varchar(255) not null,
          description varchar(255) null,
          price decimal(10, 2) not null
        )
      """
    )

  override def down: F[Int] = dropTable("Items")
}
