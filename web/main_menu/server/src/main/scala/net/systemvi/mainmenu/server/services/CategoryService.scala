package net.systemvi.mainmenu.server.services

import cats.*
import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.CategoryContext
import net.systemvi.mainmenu.server.persistance.models.Category

import java.util.UUID

trait CategoryService[F[_]] {
  def create(name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID]): F[Int]
  def get(uuid: UUID): F[Option[Category]]
  def getAll(): F[List[Category]]
  def getByRestaurant(restaurantId: UUID): F[List[Category]]
  def update(uuid: UUID, name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID]): F[Int]
  def delete(uuid: UUID): F[Int]
}

object CategoryService {
  def create[F[_]: MonadCancelThrow](categories: CategoryContext[F]): CategoryService[F] = new CategoryService[F] {
    override def create(name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID]): F[Int] =
      categories.add(Category(UUID.randomUUID(), name, description, imageUrl, parentId, restaurantId))

    override def get(uuid: UUID): F[Option[Category]] =
      categories.get(uuid)

    override def getAll(): F[List[Category]] =
      categories.getAll()

    override def getByRestaurant(restaurantId: UUID): F[List[Category]] =
      categories.getByRestaurant(restaurantId)

    override def update(uuid: UUID, name: String, description: Option[String], imageUrl: String, parentId: Option[UUID], restaurantId: Option[UUID]): F[Int] =
      categories.update(Category(uuid, name, description, imageUrl, parentId, restaurantId))

    override def delete(uuid: UUID): F[Int] =
      categories.delete(uuid)
  }
}
