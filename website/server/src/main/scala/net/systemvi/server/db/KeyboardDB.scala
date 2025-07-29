package net.systemvi.server.db

import cats.effect.IO
import doobie.*
import doobie.implicits.*
import cats.*
import cats.data.*
import cats.effect.IO
import cats.implicits.*
import net.systemvi.common.model.Keyboard
import net.systemvi.server.utils.getLogger

case class KeyboardData()

class KeyboardDB {
  
  val transactor=Transactor.fromDriverManager[IO](
    driver = "org.sqlite.JDBC",
    url = "jdbc:sqlite:sample.db",
    logHandler = None
  )
  
  def createTable():IO[Unit] = ???
  
  def dropTable():IO[Unit] = ???

  def all(): IO[List[Keyboard]] = ???
//    for{
//    result<-sql""
//  }yield()

  def add(data: KeyboardData): IO[Keyboard] = ???
  
  def get(id: Int): IO[Keyboard] = ???
}
