package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

class __05_create_restaurants_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists Restaurants(
          uuid UUID primary key,
          name varchar(255) not null,
          description varchar(255) null,
          address varchar(255) not null,
          imageUrl varchar(255) not null
        )
      """
    )

  override def down: F[Int] = dropTable("Restaurants")
}
