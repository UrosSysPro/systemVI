package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import cats.syntax.functor.*
import doobie.*
import doobie.implicits.*
import net.systemvi.mainmenu.server.persistance.models.Item
import net.systemvi.mainmenu.server.persistance.mappings.SqlMappings.given

import java.util.UUID

trait ItemContext[F[_]] {
  def add(item: Item): F[Int]
  def get(uuid: UUID): F[Option[Item]]
  def getAll(): F[List[Item]]
  def getByCategory(categoryId: UUID): F[List[Item]]
  def update(item: Item): F[Int]
  def delete(uuid: UUID): F[Int]
  /** The stored image bytes, or `None` when the item has no uploaded image. */
  def getImage(uuid: UUID): F[Option[Array[Byte]]]
  /** Stores the image bytes, replacing any previous upload. */
  def updateImage(uuid: UUID, bytes: Array[Byte]): F[Int]
}

object ItemContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F]): ItemContext[F] = new ItemContext[F] {
    override def add(item: Item): F[Int] =
      sql"""
        insert into Items(uuid, name, imageUrl, description, price, categoryId)
        values (
          ${item.uuid},
          ${item.name},
          ${item.imageUrl},
          ${item.description},
          ${item.price},
          ${item.categoryId}
        );
      """.update.run.transact(xa)

    override def get(uuid: UUID): F[Option[Item]] =
      sql"""
        select uuid, name, imageUrl, description, price, categoryId
        from Items where uuid = ${uuid.toString}
      """.query[Item].option.transact(xa)

    override def getAll(): F[List[Item]] =
      sql"""
        select uuid, name, imageUrl, description, price, categoryId
        from Items
      """.query[Item].to[List].transact(xa)

    override def getByCategory(categoryId: UUID): F[List[Item]] =
      sql"""
        select uuid, name, imageUrl, description, price, categoryId
        from Items where categoryId = ${categoryId.toString}
      """.query[Item].to[List].transact(xa)

    override def update(item: Item): F[Int] =
      sql"""
        update Items set
          name = ${item.name},
          imageUrl = ${item.imageUrl},
          description = ${item.description},
          price = ${item.price},
          categoryId = ${item.categoryId}
        where uuid = ${item.uuid};
      """.update.run.transact(xa)

    override def delete(uuid: UUID): F[Int] =
      sql"""
        delete from Items where uuid = ${uuid.toString}
      """.update.run.transact(xa)

    // The blob is read and written on its own so listings never carry image bytes.
    override def getImage(uuid: UUID): F[Option[Array[Byte]]] =
      sql"""
        select imageBlob from Items where uuid = ${uuid.toString}
      """.query[Option[Array[Byte]]].option.transact(xa).map(_.flatten)

    override def updateImage(uuid: UUID, bytes: Array[Byte]): F[Int] =
      sql"""
        update Items set imageBlob = $bytes where uuid = ${uuid.toString}
      """.update.run.transact(xa)
  }
}
