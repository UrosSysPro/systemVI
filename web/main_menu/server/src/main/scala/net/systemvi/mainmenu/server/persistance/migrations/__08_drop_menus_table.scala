package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

class __08_drop_menus_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] = dropTable("Menus")

  override def down: F[Int] =
    execute(
      sql"""
        create table if not exists Menus(
          uuid UUID primary key,
          name varchar(255) not null,
          description varchar(255) null
        )
      """
    )
}
