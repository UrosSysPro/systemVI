package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

class __03_create_categories_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists Categories(
          uuid UUID primary key,
          name varchar(255) not null,
          description varchar(255) null,
          imageUrl varchar(255) not null,
          parentId UUID null
        )
      """
    )

  override def down: F[Int] = dropTable("Categories")
}
