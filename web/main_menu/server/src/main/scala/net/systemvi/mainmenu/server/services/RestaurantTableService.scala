package net.systemvi.mainmenu.server.services

import cats.*
import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.RestaurantTableContext
import net.systemvi.mainmenu.server.persistance.models.RestaurantTable

import java.util.UUID

trait RestaurantTableService[F[_]] {
  def create(name: String, imageUrl: String, restaurantId: UUID): F[Int]
  def get(uuid: UUID): F[Option[RestaurantTable]]
  def getAll(): F[List[RestaurantTable]]
  def getByRestaurant(restaurantId: UUID): F[List[RestaurantTable]]
  def update(uuid: UUID, name: String, imageUrl: String, restaurantId: UUID): F[Int]
  def delete(uuid: UUID): F[Int]
}

object RestaurantTableService {
  def create[F[_]: MonadCancelThrow](tables: RestaurantTableContext[F]): RestaurantTableService[F] = new RestaurantTableService[F] {
    override def create(name: String, imageUrl: String, restaurantId: UUID): F[Int] =
      tables.add(RestaurantTable(UUID.randomUUID(), name, imageUrl, restaurantId))

    override def get(uuid: UUID): F[Option[RestaurantTable]] =
      tables.get(uuid)

    override def getAll(): F[List[RestaurantTable]] =
      tables.getAll()

    override def getByRestaurant(restaurantId: UUID): F[List[RestaurantTable]] =
      tables.getByRestaurant(restaurantId)

    override def update(uuid: UUID, name: String, imageUrl: String, restaurantId: UUID): F[Int] =
      tables.update(RestaurantTable(uuid, name, imageUrl, restaurantId))

    override def delete(uuid: UUID): F[Int] =
      tables.delete(uuid)
  }
}
