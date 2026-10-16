package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import cats.syntax.functor.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.Category
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait CategoryContext[F[_]] {
  def add(category: Category): F[Int]
  def get(uuid: UUID): F[Option[Category]]
  def getAll(): F[List[Category]]
  def getByRestaurant(restaurantId: UUID): F[List[Category]]
  def update(category: Category): F[Int]
  def delete(uuid: UUID): F[Int]
  /** The stored image bytes, or `None` when the category has no uploaded image. */
  def getImage(uuid: UUID): F[Option[Array[Byte]]]
  /** Stores the image bytes, replacing any previous upload. */
  def updateImage(uuid: UUID, bytes: Array[Byte]): F[Int]
}

object CategoryContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): CategoryContext[F] = new CategoryContext[F] {
    override def add(category: Category): F[Int] =
      sql"""
        insert into Categories(uuid, name, description, imageUrl, parentId, restaurantId)
        values (
          ${category.uuid},
          ${category.name},
          ${category.description},
          ${category.imageUrl},
          ${category.parentId},
          ${category.restaurantId}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[Category]] =
      sql"""
        select uuid, name, description, imageUrl, parentId, restaurantId
        from Categories where uuid = ${uuid.toString}
      """.query[Category].option.transact(xa)

    override def getAll(): F[List[Category]] =
      sql"""
        select uuid, name, description, imageUrl, parentId, restaurantId
        from Categories
      """.query[Category].to[List].transact(xa)

    override def getByRestaurant(restaurantId: UUID): F[List[Category]] =
      sql"""
        select uuid, name, description, imageUrl, parentId, restaurantId
        from Categories where restaurantId = ${restaurantId.toString}
      """.query[Category].to[List].transact(xa)

    override def update(category: Category): F[Int] =
      sql"""
        update Categories set
          name = ${category.name},
          description = ${category.description},
          imageUrl = ${category.imageUrl},
          parentId = ${category.parentId},
          restaurantId = ${category.restaurantId}
        where uuid = ${category.uuid};
      """.update.run.transact(xa)

    override def delete(uuid: UUID): F[Int] =
      sql"""
        delete from Categories where uuid = ${uuid.toString}
      """.update.run.transact(xa)

    // The blob is read and written on its own so listings never carry image bytes.
    override def getImage(uuid: UUID): F[Option[Array[Byte]]] =
      sql"""
        select imageBlob from Categories where uuid = ${uuid.toString}
      """.query[Option[Array[Byte]]].option.transact(xa).map(_.flatten)

    override def updateImage(uuid: UUID, bytes: Array[Byte]): F[Int] =
      sql"""
        update Categories set imageBlob = $bytes where uuid = ${uuid.toString}
      """.update.run.transact(xa)
  }
}
