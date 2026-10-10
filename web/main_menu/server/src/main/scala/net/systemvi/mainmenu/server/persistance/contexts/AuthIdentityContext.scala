package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.{AuthIdentity, AuthProvider}
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait AuthIdentityContext[F[_]] {
  def add(identity: AuthIdentity): F[Int]
  def get(uuid: UUID): F[Option[AuthIdentity]]
  def getByProviderSubject(provider: AuthProvider, providerSubject: String): F[Option[AuthIdentity]]
  def getByUser(userId: UUID): F[List[AuthIdentity]]
  def delete(uuid: UUID): F[Int]
}

object AuthIdentityContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): AuthIdentityContext[F] = new AuthIdentityContext[F] {
    override def add(identity: AuthIdentity): F[Int] =
      sql"""
        insert into AuthIdentities(uuid, userId, provider, providerSubject, email)
        values (
          ${identity.uuid},
          ${identity.userId},
          ${identity.provider},
          ${identity.providerSubject},
          ${identity.email}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[AuthIdentity]] =
      sql"""
        select uuid, userId, provider, providerSubject, email
        from AuthIdentities where uuid = ${uuid.toString}
      """.query[AuthIdentity].option.transact(xa)

    override def getByProviderSubject(provider: AuthProvider, providerSubject: String): F[Option[AuthIdentity]] =
      sql"""
        select uuid, userId, provider, providerSubject, email
        from AuthIdentities where provider = $provider and providerSubject = $providerSubject
      """.query[AuthIdentity].option.transact(xa)

    override def getByUser(userId: UUID): F[List[AuthIdentity]] =
      sql"""
        select uuid, userId, provider, providerSubject, email
        from AuthIdentities where userId = ${userId.toString}
      """.query[AuthIdentity].to[List].transact(xa)

    override def delete(uuid: UUID): F[Int] =
      sql"""
        delete from AuthIdentities where uuid = ${uuid.toString}
      """.update.run.transact(xa)
  }
}
