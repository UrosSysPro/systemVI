package net.systemvi.tests.manufacturers

import java.util.UUID

import cats.*
import cats.implicits.*
import cats.data.*
import cats.effect.*
import cats.effect.implicits.*

import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.util.transactor.Transactor.*

def sqlite() = IO{
  Transactor.fromDriverManager[IO](
    "org.sqlite.JDBC",
    "jdbc:sqlite:test.db",
    None
  )
}

object Main extends IOApp.Simple {
  override def run: IO[Unit] = for {
    _   <- IO.println("creating db connection")
    xa  <- sqlite()
    db  = ManufacturerDB.create(xa)

    _   <- IO.println("try create table")
    _   <- db.createTable()

    _   <- IO.println("add data")
    _   <- List("Kailh","Gateron","Keychron")
            .map(name=>db.add(Manufacturer(UUID.randomUUID(),name)))
            .sequence

    _   <- IO.println("read data")
    manufacturers <- db.get()
    _   <- manufacturers
            .map(m=>IO.println(m.name))
            .sequence

  } yield {}
}
