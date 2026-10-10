package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.Restaurant
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait RestaurantContext[F[_]] {
  def add(restaurant: Restaurant): F[Int]
  def get(uuid: UUID): F[Option[Restaurant]]
  def getAll(): F[List[Restaurant]]
  def update(restaurant: Restaurant): F[Int]
  def delete(uuid: UUID): F[Int]
}

object RestaurantContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): RestaurantContext[F] = new RestaurantContext[F] {
    override def add(restaurant: Restaurant): F[Int] =
      sql"""
        insert into Restaurants(uuid, name, description, address, imageUrl)
        values (
          ${restaurant.uuid},
          ${restaurant.name},
          ${restaurant.description},
          ${restaurant.address},
          ${restaurant.imageUrl}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[Restaurant]] =
      sql"""
        select uuid, name, description, address, imageUrl
        from Restaurants where uuid = ${uuid.toString}
      """.query[Restaurant].option.transact(xa)

    override def getAll(): F[List[Restaurant]] =
      sql"""
        select uuid, name, description, address, imageUrl
        from Restaurants
      """.query[Restaurant].to[List].transact(xa)

    override def update(restaurant: Restaurant): F[Int] =
      sql"""
        update Restaurants set
          name = ${restaurant.name},
          description = ${restaurant.description},
          address = ${restaurant.address},
          imageUrl = ${restaurant.imageUrl}
        where uuid = ${restaurant.uuid};
      """.update.run.transact(xa)

    override def delete(uuid: UUID): F[Int] =
      sql"""
        delete from Restaurants where uuid = ${uuid.toString}
      """.update.run.transact(xa)
  }
}
