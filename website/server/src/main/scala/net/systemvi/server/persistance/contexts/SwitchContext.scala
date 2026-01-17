package net.systemvi.server.persistance.contexts

import cats.effect.*
import doobie.*
import doobie.implicits.*
import doobie.generic.auto.*
import doobie.h2.*
import net.systemvi.server.persistance.models.*
import net.systemvi.server.persistance.mappings.*
import java.util.UUID

trait SwitchContext[F[_]] {
  def add(switch:Switch):F[Int]
  def get(uuid:UUID):F[Option[Switch]]
  def get():F[List[Switch]]
  def remove(uuid:UUID):F[Int]
}

object SwitchContext{
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): SwitchContext[F] = {
    new SwitchContext[F]{
      import SqlMappings.given

      override def add(switch: Switch): F[Int] =
        sql"""
             |insert into Switches(uuid,manufacturerUUID,switchTypeId,name)
             |values (${switch.uuid},${switch.manufacturerUUID},${switch.switchTypeId},${switch.name})
           """.stripMargin('|').update.run.transact(xa)

      override def get(uuid: UUID): F[Option[Switch]] =
        sql"""
             |select *
             |from Switches
             |where uuid = $uuid
           """.stripMargin('|').query[Switch].option.transact(xa)

      override def get(): F[List[Switch]] =
        sql"""
             |select *
             |from Switches
           """.stripMargin('|').query[Switch].to[List].transact(xa)

      override def remove(uuid: UUID): F[Int] =
        sql"""
             |delete from Switches
             |where uuid = $uuid
           """.stripMargin('|').update.run.transact(xa)
    }
  }
}
