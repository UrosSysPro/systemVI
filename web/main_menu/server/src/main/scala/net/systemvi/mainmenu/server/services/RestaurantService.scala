package net.systemvi.mainmenu.server.services

import cats.*
import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.RestaurantContext
import net.systemvi.mainmenu.server.persistance.models.Restaurant

import java.util.UUID

trait RestaurantService[F[_]] {
  def create(name: String, description: Option[String], address: String, imageUrl: String): F[Int]
  def get(uuid: UUID): F[Option[Restaurant]]
  def getAll(): F[List[Restaurant]]
  def update(uuid: UUID, name: String, description: Option[String], address: String, imageUrl: String): F[Int]
  def delete(uuid: UUID): F[Int]
}

object RestaurantService {
  def create[F[_]: MonadCancelThrow](restaurants: RestaurantContext[F]): RestaurantService[F] = new RestaurantService[F] {
    override def create(name: String, description: Option[String], address: String, imageUrl: String): F[Int] =
      restaurants.add(Restaurant(UUID.randomUUID(), name, description, address, imageUrl))

    override def get(uuid: UUID): F[Option[Restaurant]] =
      restaurants.get(uuid)

    override def getAll(): F[List[Restaurant]] =
      restaurants.getAll()

    override def update(uuid: UUID, name: String, description: Option[String], address: String, imageUrl: String): F[Int] =
      restaurants.update(Restaurant(uuid, name, description, address, imageUrl))

    override def delete(uuid: UUID): F[Int] =
      restaurants.delete(uuid)
  }
}
