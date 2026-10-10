package net.systemvi.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.mappings.*
import java.util.UUID

trait EntityImageContext[F[_]] {
  def add(image: EntityImage):F[Int]
  def get(uuid:UUID):F[List[EntityImage]]
}

object EntityImageContext{
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): EntityImageContext[F] = {
    new EntityImageContext[F]{
      import SqlMappings.given

      override def add(image: EntityImage): F[Int] =
        sql"""
             |insert into EntityImages(entityUUID,imageUrl,"order")
             |values (${image.entityUUID},${image.imageUrl},${image.order})
             |
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[List[EntityImage]] =
        sql"""
             |select *
             |from EntityImages
             |where entityUUID = $uuid
           """.stripMargin('|').query[EntityImage].to[List].transact(xa)
    }
  }
}
