package net.systemvi.website.server.persistance.contexts

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import net.systemvi.website.server.persistance.contexts.*
import net.systemvi.website.server.persistance.models.*
import net.systemvi.website.server.persistance.mappings.*
import net.systemvi.website.server.persistance.models.User

import java.util.UUID

trait UserContext[F[_]] {
  def add(user: User): F[Int]
  def get(uuid: UUID): F[Option[User]]
}

object UserContext {
  def create[F[_] : MonadCancelThrow](xa: Transactor[F]): UserContext[F] = new UserContext[F] {
    import net.systemvi.website.server.persistance.mappings.SqlMappings.given

    override def add(user: User): F[Int] =
      sql"""
        insert into Users(uuid, email, name, picture)
        values (
          ${user.uuid},
          ${user.email},
          ${user.name},
          ${user.picture}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[User]] =
      sql"""
        select * from Users where uuid = ${uuid.toString}
      """.query[User].option.transact(xa)
  }
}
