package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.User
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait UserContext[F[_]] {
  def add(user: User): F[Int]
  def get(uuid: UUID): F[Option[User]]
  def getAll(): F[List[User]]
  def getByEmail(email: String): F[Option[User]]
  def update(user: User): F[Int]
  def delete(uuid: UUID): F[Int]
}

object UserContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): UserContext[F] = new UserContext[F] {
    override def add(user: User): F[Int] =
      sql"""
        insert into Users(uuid, roleId, status, name, email)
        values (
          ${user.uuid},
          ${user.role},
          ${user.status},
          ${user.name},
          ${user.email}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[User]] =
      sql"""
        select uuid, roleId, status, name, email
        from Users where uuid = ${uuid.toString}
      """.query[User].option.transact(xa)

    override def getAll(): F[List[User]] =
      sql"""
        select uuid, roleId, status, name, email
        from Users
      """.query[User].to[List].transact(xa)

    override def getByEmail(email: String): F[Option[User]] =
      sql"""
        select uuid, roleId, status, name, email
        from Users where email = $email
      """.query[User].option.transact(xa)

    override def update(user: User): F[Int] =
      sql"""
        update Users set
          roleId = ${user.role},
          status = ${user.status},
          name = ${user.name},
          email = ${user.email}
        where uuid = ${user.uuid};
      """.update.run.transact(xa)

    override def delete(uuid: UUID): F[Int] =
      sql"""
        delete from Users where uuid = ${uuid.toString}
      """.update.run.transact(xa)
  }
}
