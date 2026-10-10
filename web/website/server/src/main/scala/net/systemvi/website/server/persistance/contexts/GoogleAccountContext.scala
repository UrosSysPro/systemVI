package net.systemvi.website.server.persistance.contexts

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import net.systemvi.website.server.persistance.mappings.SqlMappings
import net.systemvi.website.server.persistance.models.{GoogleAccount, User}
import net.systemvi.website.server.services.GoogleUserProfile

trait GoogleAccountContext[F[_]] {
  def add(googleUserProfile: GoogleUserProfile, user: User): F[GoogleAccount]
  def get(sub: String): F[Option[GoogleAccount]]
}

object GoogleAccountContext {
  import SqlMappings.given
  
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]) = new GoogleAccountContext[F] {
    override def add(googleUserProfile: GoogleUserProfile, user: User): F[GoogleAccount] = {
      val account = GoogleAccount(
        googleUserProfile.sub,
        user.uuid,
        googleUserProfile.email,
        googleUserProfile.email_verified,
        googleUserProfile.name,
        googleUserProfile.picture,
        googleUserProfile.given_name,
        googleUserProfile.family_name,
        googleUserProfile.locale
      )
      for{
        _ <- sql"""
            insert into GoogleAccounts(sub, userUUID, email, email_verified, name, picture, given_name, family_name, locale)
            values(
              ${googleUserProfile.sub},
              ${account.userUUID},
              ${googleUserProfile.email},
              ${googleUserProfile.email_verified},
              ${googleUserProfile.name},
              ${googleUserProfile.picture},
              ${googleUserProfile.given_name},
              ${googleUserProfile.family_name},
              ${googleUserProfile.locale}
            );
      """.update.run.transact(xa)
      } yield account
    }

    override def get(sub: String): F[Option[GoogleAccount]] = {
      sql"""
        select * from GoogleAccounts where sub = $sub;    
      """.query[GoogleAccount].option.transact(xa)
    }
  }
}
