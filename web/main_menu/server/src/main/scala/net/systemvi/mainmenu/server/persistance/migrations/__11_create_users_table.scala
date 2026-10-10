package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

/**
 * Creates the `Users` table.
 *
 * A user always has a role; the role is stored as the [[Role]] id (see the
 * `Roles` table) and the status as the [[UserStatus]] id. Customers start as
 * `ANONYMOUS` and turn `ACTIVE` once a sign-in identity is attached; staff are
 * created `ACTIVE` up front and become usable when the matching sign-in first
 * arrives.
 */
class __11_create_users_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists Users(
          uuid UUID primary key,
          roleId integer not null,
          status integer not null,
          name varchar(255),
          email varchar(255)
        )
      """
    )

  override def down: F[Int] = dropTable("Users")
}
