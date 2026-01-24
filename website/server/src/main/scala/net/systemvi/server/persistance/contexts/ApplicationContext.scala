package net.systemvi.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.mappings.*

import java.util.UUID

trait ApplicationContext[F[_]] {
  def add(application:Application):F[Int]
  def get(uuid:UUID):F[Option[Application]]
  def get():F[List[Application]]
  def remove(uuid:UUID):F[Int]
}

object ApplicationContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): ApplicationContext[F] = {
    new ApplicationContext[F]{
      import SqlMappings.given

      override def add(application: Application): F[Int] =
        sql"""
             |insert into Applications(uuid,categoryId,name,codeName,description)
             |values (${application.uuid},${application.categoryId},${application.name},${application.codeName},${application.description})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Application]] =
        sql"""
             |select *
             |from Applications
             |where uuid = $uuid
           """.stripMargin('|').query[Application].option.transact(xa)

      override def get(): F[List[Application]] =
        sql"""
             |select *
             |from Applications
           """.stripMargin('|').query[Application].to[List].transact(xa)

      override def remove(uuid: UUID): F[Int] =
        sql"""
             |delete from Applications
             |where uuid = $uuid
           """.stripMargin('|').update.run.transact(xa)
    }
  }
}
