package net.systemvi.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.mappings.*
import java.util.UUID

trait KeyboardContext[F[_]] {
  def add(keyboard:Keyboard):F[Int]
  def get(uuid:UUID):F[Option[Keyboard]]
  def get():F[List[Keyboard]]
  def remove(uuid:UUID):F[Int]
}

object KeyboardContext{
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): KeyboardContext[F] = {
    new KeyboardContext[F]{
      import SqlMappings.given

      override def add(keyboard: Keyboard): F[Int] =
        sql"""
             |insert into Keyboards(uuid,switchUUID,profileId,filamentUUID,name,codeName)
             |values (${keyboard.uuid},${keyboard.switchUUID},${keyboard.profileId},${keyboard.filamentUUID},${keyboard.name},${keyboard.codeName})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Keyboard]] =
        sql"""
             |select *
             |from Keyboards
             |where uuid = $uuid
           """.stripMargin('|').query[Keyboard].option.transact(xa)

      override def get(): F[List[Keyboard]] =
        sql"""
             |select *
             |from Keyboards
           """.stripMargin('|').query[Keyboard].to[List].transact(xa)

      override def remove(uuid: UUID): F[Int] =
        sql"""
             |delete from Keyboards
             |where uuid = $uuid
           """.stripMargin('|').update.run.transact(xa)
    }
  }
}
