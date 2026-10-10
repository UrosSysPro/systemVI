package net.systemvi.website.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.website.server.persistance.models.*
import net.systemvi.website.server.persistance.mappings.*
import net.systemvi.website.server.persistance.models.Manufacturer

import java.util.UUID

trait ManufacturerContext[F[_]] {
  def add(manufacturer:Manufacturer):F[Int]
  def get(uuid:UUID):F[Option[Manufacturer]]
  def get():F[List[Manufacturer]]
  def remove(uuid:UUID):F[Int]
}

object ManufacturerContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): ManufacturerContext[F] = {
    new ManufacturerContext[F]{
      import net.systemvi.website.server.persistance.mappings.SqlMappings.given

      override def add(manufacturer: Manufacturer): F[Int] =
        sql"""
          |insert into Manufacturers(uuid,name)
          |values (${manufacturer.uuid},${manufacturer.name})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Manufacturer]] =
        sql"""
          |select *
          |from Manufacturers
          |where uuid = $uuid
           """.stripMargin('|').query[Manufacturer].option.transact(xa)

      override def get(): F[List[Manufacturer]] =
        sql"""
             |select *
             |from Manufacturers
           """.stripMargin('|').query[Manufacturer].to[List].transact(xa)

      override def remove(uuid: UUID): F[Int] =
        sql"""
             |delete from Manufacturers
             |where uuid = $uuid
           """.stripMargin('|').update.run.transact(xa)
    }
  }
}
