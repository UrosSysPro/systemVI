package net.systemvi.mainmenu.server.persistance.seeders

import cats.*
import cats.implicits.*
import cats.effect.*
import doobie.util.transactor.Transactor
import net.systemvi.mainmenu.server.persistance.models.Menu
import net.systemvi.mainmenu.server.persistance.contexts.MenuContext

import java.util.UUID

object MenuSeeders {
  def seed[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Unit] = {
    val menus = MenuContext.create(xa)

    val samples = List(
      Menu(UUID.randomUUID(), "Main Menu", Some("The primary navigation menu")),
      Menu(UUID.randomUUID(), "Settings", Some("Application settings")),
      Menu(UUID.randomUUID(), "About", None),
    )

    for {
      _ <- samples.traverse(menus.add)
    } yield ()
  }
}
