package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.Role

/**
 * Creates the `Roles` lookup table and seeds the three fixed roles.
 *
 * Roles are a closed set (see [[Role.all]]), so they are inserted here rather
 * than by a seeder: every database that has migrations applied gets them.
 */
class __09_create_roles_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists Roles(
          id integer primary key,
          name varchar(255) not null unique
        )
      """
    ) *> Role.all.traverse_(role =>
      execute(sql"insert into Roles(id, name) values (${role.id}, ${role.name})")
    ) *> MonadCancelThrow[F].pure(0)

  override def down: F[Int] = dropTable("Roles")
}
