package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import doobie.*
import doobie.implicits.*

/**
 * Creates the `AuthIdentities` table.
 *
 * A row links one external sign-in (a Google account for now) to a [[User]].
 * The pair (provider, providerSubject) is unique so a returning sign-in always
 * resolves to the same account, whatever device it comes from. `provider` stores
 * the [[AuthProvider]] id.
 */
class __10_create_auth_identities_table[F[_]: MonadCancelThrow](xa: Transactor[F]) extends BaseMigration[F](xa) {
  override def up: F[Int] =
    execute(
      sql"""
        create table if not exists AuthIdentities(
          uuid UUID primary key,
          userId UUID not null,
          provider integer not null,
          providerSubject varchar(255) not null,
          email varchar(255),
          unique(provider, providerSubject)
        )
      """
    )

  override def down: F[Int] = dropTable("AuthIdentities")
}
