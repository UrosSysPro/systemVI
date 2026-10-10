package net.systemvi.website.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.website.server.persistance.models.*
import net.systemvi.website.server.persistance.mappings.*
import net.systemvi.website.server.persistance.models.Filament

import java.util.UUID

trait FilamentContext[F[_]] {
  def add(filament: Filament):F[Int]
  def get(uuid:UUID):F[Option[Filament]]
  def get():F[List[Filament]]
  def remove(uuid:UUID):F[Int]
}

object FilamentContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): FilamentContext[F] = {
    new FilamentContext[F]{
      import net.systemvi.website.server.persistance.mappings.SqlMappings.given

      override def add(filament: Filament): F[Int] =
        sql"""
             |insert into Filaments(uuid,polymerId,manufacturerUUID,name)
             |values (${filament.uuid},${filament.polymerId},${filament.manufacturerUUID},${filament.name})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Filament]] =
        sql"""
             |select *
             |from Filaments
             |where uuid = $uuid
           """.stripMargin('|').query[Filament].option.transact(xa)

      override def get(): F[List[Filament]] =
        sql"""
             |select *
             |from Filaments
           """.stripMargin('|').query[Filament].to[List].transact(xa)

      override def remove(uuid: UUID): F[Int] =
        sql"""
             |delete from Filaments
             |where uuid = $uuid
           """.stripMargin('|').update.run.transact(xa)
    }
  }
}
