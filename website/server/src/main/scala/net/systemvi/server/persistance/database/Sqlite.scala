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

val sqlite:Resource[IO,Transactor[IO]] = for{
  ec <- ExecutionContexts.fixedThreadPool[IO](32)
  xa <- HikariTransactor.newHikariTransactor[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:test.db",
    "",
    "",
    ec
  )
}yield xa