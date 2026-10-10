package net.systemvi.mainmenu.server.services

import cats.*
import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.MenuContext
import net.systemvi.mainmenu.server.persistance.models.Menu

import java.util.UUID

trait MenuService[F[_]] {
  def create(name: String, description: Option[String]): F[Int]
  def get(uuid: UUID): F[Option[Menu]]
  def getAll(): F[List[Menu]]
}

object MenuService {
  def create[F[_]: MonadCancelThrow](menus: MenuContext[F]): MenuService[F] = new MenuService[F] {
    override def create(name: String, description: Option[String]): F[Int] =
      menus.add(Menu(UUID.randomUUID(), name, description))

    override def get(uuid: UUID): F[Option[Menu]] =
      menus.get(uuid)

    override def getAll(): F[List[Menu]] =
      menus.getAll()
  }
}
