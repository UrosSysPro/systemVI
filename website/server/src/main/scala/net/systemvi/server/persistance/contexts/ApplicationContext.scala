//package net.systemvi.server.persistance.contexts
//
//import cats.effect.*
//import doobie.*
//import doobie.implicits.*
//import doobie.generic.auto.*
//import doobie.h2.*
//import net.systemvi.server.persistance.models.*
//import net.systemvi.server.persistance.mappings.*
//
//import java.util.UUID
//
//trait ApplicationContext[F[_]] {
//  def add(application:Application):F[Int]
//  def get(uuid:UUID):F[Option[Application]]
//  def get():F[List[Application]]
//  def remove(uuid:UUID):F[Int]
//}
//
//object ApplicationContext {
//  def create[F[_]:MonadCancelThrow](xa:Transactor[F]): ApplicationContext[F] = {
//    new ApplicationContext[F]{
//      import SqlMappings.given
//
//      override def add(application: Application): F[Int] =
//        sql"""
//             |insert into Applications(uuid,,name)
//             |values (${manufacturer.uuid},${manufacturer.name})
//           """.stripMargin('|').update.run.transact(xa)
//
//      override def get(uuid: UUID): F[Option[Manufacturer]] =
//        sql"""
//             |select *
//             |from Manufacturers
//             |where uuid = $uuid
//           """.stripMargin('|').query[Manufacturer].option.transact(xa)
//
//      override def get(): F[List[Manufacturer]] =
//        sql"""
//             |select *
//             |from Manufacturers
//           """.stripMargin('|').query[Manufacturer].to[List].transact(xa)
//
//      override def remove(uuid: UUID): F[Int] =
//        sql"""
//             |delete from Manufacturers
//             |where uuid = $uuid
//           """.stripMargin('|').update.run.transact(xa)
//    }
//  }
//}
