package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.Role
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

trait RoleContext[F[_]] {
  def getAll(): F[List[Role]]
  def get(id: Int): F[Option[Role]]
  def getByName(name: String): F[Option[Role]]
}

object RoleContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): RoleContext[F] = new RoleContext[F] {
    override def getAll(): F[List[Role]] =
      sql"""
        select id
        from Roles order by id
      """.query[Role].to[List].transact(xa)

    override def get(id: Int): F[Option[Role]] =
      sql"""
        select id
        from Roles where id = $id
      """.query[Role].option.transact(xa)

    override def getByName(name: String): F[Option[Role]] =
      sql"""
        select id
        from Roles where name = $name
      """.query[Role].option.transact(xa)
  }
}
