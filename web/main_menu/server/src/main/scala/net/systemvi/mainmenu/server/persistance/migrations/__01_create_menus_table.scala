package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

class __01_create_menus_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists Menus(
          uuid UUID primary key,
          name varchar(255) not null,
          description varchar(255) null
        )
      """
    )

  override def down: F[Int] = dropTable("Menus")
}
