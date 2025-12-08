package net.systemvi.tests.full_db_test

import cats.effect.*
import cats.effect.implicits.*
import cats.*
import cats.implicits.*
import cats.effect.Resource
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

import scala.concurrent.ExecutionContext

val sqlite:Resource[IO,HikariTransactor[IO]] = for{
  executionContext <- ExecutionContexts.fixedThreadPool[IO](32)
  xa <- HikariTransactor.newHikariTransactor[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:test.db",
    "",
    "",
    executionContext
  )
}yield xa
