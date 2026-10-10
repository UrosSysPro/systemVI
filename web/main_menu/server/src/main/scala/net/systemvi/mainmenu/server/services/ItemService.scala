package net.systemvi.mainmenu.server.services

import cats.*
import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.ItemContext
import net.systemvi.mainmenu.server.persistance.models.Item

import java.util.UUID

trait ItemService[F[_]] {
  def create(name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID]): F[Int]
  def get(uuid: UUID): F[Option[Item]]
  def getAll(): F[List[Item]]
  def getByCategory(categoryId: UUID): F[List[Item]]
  def update(uuid: UUID, name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID]): F[Int]
  def delete(uuid: UUID): F[Int]
}

object ItemService {
  def create[F[_]: MonadCancelThrow](items: ItemContext[F]): ItemService[F] = new ItemService[F] {
    override def create(name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID]): F[Int] =
      items.add(Item(UUID.randomUUID(), name, imageUrl, description, price, categoryId))

    override def get(uuid: UUID): F[Option[Item]] =
      items.get(uuid)

    override def getAll(): F[List[Item]] =
      items.getAll()

    override def getByCategory(categoryId: UUID): F[List[Item]] =
      items.getByCategory(categoryId)

    override def update(uuid: UUID, name: String, imageUrl: String, description: Option[String], price: BigDecimal, categoryId: Option[UUID]): F[Int] =
      items.update(Item(uuid, name, imageUrl, description, price, categoryId))

    override def delete(uuid: UUID): F[Int] =
      items.delete(uuid)
  }
}
