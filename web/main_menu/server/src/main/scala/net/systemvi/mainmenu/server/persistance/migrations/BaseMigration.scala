package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

/**
 * Base class for every migration.
 *
 * A migration is a single, self-contained change to the database schema with an
 * `up` (apply) and a `down` (revert) step. [[MigrationRunner]] keeps track of the
 * migrations that have been applied so `migrate` only runs the remaining ones and
 * `migrate down` reverts the most recently applied one.
 *
 * Every migration lives in this package and is named with an ordering prefix,
 * e.g. `__01_create_menus_table` or `__04_alter_items_add_categoryId_column`.
 */
abstract class BaseMigration[F[_]: MonadCancelThrow](protected val xa: Transactor[F]) {

  /** Name recorded in the `SchemaMigrations` table; defaults to the class name. */
  def name: String = getClass.getSimpleName.stripSuffix("$")

  /** Applies the migration. */
  def up: F[Int]

  /** Reverts the migration. */
  def down: F[Int]

  /** Runs a raw statement, e.g. `sql"create table if not exists ..."`. */
  protected def execute(statement: Fragment): F[Int] =
    statement.update.run.transact(xa)

  // The following helpers build DDL that SQL does not allow to be parameterised,
  // so identifiers are inlined. Arguments are migration-local literals, never
  // user input.

  protected def addColumn(table: String, column: String, definition: String): F[Int] =
    execute(Fragment.const(s"alter table $table add column $column $definition"))

  protected def dropColumn(table: String, column: String): F[Int] =
    execute(Fragment.const(s"alter table $table drop column $column"))

  protected def renameColumn(table: String, from: String, to: String): F[Int] =
    execute(Fragment.const(s"alter table $table rename column $from to $to"))

  protected def dropTable(table: String): F[Int] =
    execute(Fragment.const(s"drop table if exists $table"))
}
