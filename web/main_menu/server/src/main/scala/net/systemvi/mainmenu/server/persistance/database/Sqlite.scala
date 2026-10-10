package net.systemvi.mainmenu.server.persistance.database

import cats.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.hikari.*

def sqlite[F[_]: Async: MonadCancelThrow]: Resource[F, Transactor[F]] = for {
  ec <- ExecutionContexts.fixedThreadPool[F](32)
  xa <- HikariTransactor.newHikariTransactor[F](
    "org.sqlite.JDBC",
    "jdbc:sqlite:db/main_menu.db",
    "",
    "",
    ec
  )
} yield xa
