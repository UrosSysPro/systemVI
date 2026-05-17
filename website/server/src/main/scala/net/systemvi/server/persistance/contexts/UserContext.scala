package net.systemvi.server.persistance.contexts

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import net.systemvi.server.persistance.contexts.*
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.mappings.*

import java.util.UUID

trait UserContext[F[_]] {
  def add(user: User): F[Int]
  def get(uuid: UUID): F[Option[User]]
}

object UserContext {
  def create[F[_] : MonadCancelThrow](xa: Transactor[F]): UserContext[F] = new UserContext[F] {
    import SqlMappings.given

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
