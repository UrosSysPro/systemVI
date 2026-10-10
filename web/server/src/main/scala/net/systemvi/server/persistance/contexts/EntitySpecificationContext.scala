package net.systemvi.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.mappings.*
import java.util.UUID

trait EntitySpecificationContext[F[_]] {
  def add(spec: EntitySpecification):F[Int]
  def get(uuid:UUID):F[List[EntitySpecification]]
}

object EntitySpecificationContext{
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): EntitySpecificationContext[F] = {
    new EntitySpecificationContext[F]{
      import SqlMappings.given

      override def add(spec: EntitySpecification): F[Int] =
        sql"""
             |insert into EntitySpecifications(entityUUID,key,value,"order")
             |values (${spec.entityUUID},${spec.key},${spec.value},${spec.order})
             |
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[List[EntitySpecification]] =
        sql"""
             |select *
             |from EntitySpecifications
             |where entityUUID = $uuid
           """.stripMargin('|').query[EntitySpecification].to[List].transact(xa)
    }
  }
}
