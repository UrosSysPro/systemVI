package net.systemvi.server.persistance.database

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import doobie.hikari.*

def sqlite[F[_]: Async: MonadCancelThrow]:Resource[F,Transactor[F]] = for{
  ec <- ExecutionContexts.fixedThreadPool[F](32)
  xa <- HikariTransactor.newHikariTransactor[F](
    "org.sqlite.JDBC",
    "jdbc:sqlite:db/test.db",
    "",
    "",
    ec
  )
}yield xa