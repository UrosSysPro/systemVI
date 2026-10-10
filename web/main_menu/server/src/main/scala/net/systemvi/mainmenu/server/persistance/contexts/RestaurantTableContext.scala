package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.RestaurantTable
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait RestaurantTableContext[F[_]] {
  def add(table: RestaurantTable): F[Int]
  def get(uuid: UUID): F[Option[RestaurantTable]]
  def getAll(): F[List[RestaurantTable]]
  def getByRestaurant(restaurantId: UUID): F[List[RestaurantTable]]
  def update(table: RestaurantTable): F[Int]
  def delete(uuid: UUID): F[Int]
}

object RestaurantTableContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): RestaurantTableContext[F] = new RestaurantTableContext[F] {
    override def add(table: RestaurantTable): F[Int] =
      sql"""
        insert into RestaurantTables(uuid, name, imageUrl, restaurantId)
        values (
          ${table.uuid},
          ${table.name},
          ${table.imageUrl},
          ${table.restaurantId}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[RestaurantTable]] =
      sql"""
        select uuid, name, imageUrl, restaurantId
        from RestaurantTables where uuid = ${uuid.toString}
      """.query[RestaurantTable].option.transact(xa)

    override def getAll(): F[List[RestaurantTable]] =
      sql"""
        select uuid, name, imageUrl, restaurantId
        from RestaurantTables
      """.query[RestaurantTable].to[List].transact(xa)

    override def getByRestaurant(restaurantId: UUID): F[List[RestaurantTable]] =
      sql"""
        select uuid, name, imageUrl, restaurantId
        from RestaurantTables where restaurantId = ${restaurantId.toString}
      """.query[RestaurantTable].to[List].transact(xa)

    override def update(table: RestaurantTable): F[Int] =
      sql"""
        update RestaurantTables set
          name = ${table.name},
          imageUrl = ${table.imageUrl},
          restaurantId = ${table.restaurantId}
        where uuid = ${table.uuid};
      """.update.run.transact(xa)

    override def delete(uuid: UUID): F[Int] =
      sql"""
        delete from RestaurantTables where uuid = ${uuid.toString}
      """.update.run.transact(xa)
  }
}
