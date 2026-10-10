package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.Menu
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait MenuContext[F[_]] {
  def add(menu: Menu): F[Int]
  def get(uuid: UUID): F[Option[Menu]]
  def getAll(): F[List[Menu]]
}

object MenuContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): MenuContext[F] = new MenuContext[F] {
    override def add(menu: Menu): F[Int] =
      sql"""
        insert into Menus(uuid, name, description)
        values (
          ${menu.uuid},
          ${menu.name},
          ${menu.description}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[Menu]] =
      sql"""
        select * from Menus where uuid = ${uuid.toString}
      """.query[Menu].option.transact(xa)

    override def getAll(): F[List[Menu]] =
      sql"""
        select * from Menus
      """.query[Menu].to[List].transact(xa)
  }
}
