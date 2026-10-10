package net.systemvi.mainmenu.common.dto

import io.circe.*
import io.circe.syntax.*

import java.util.UUID

case class CategoryDto(
  uuid: UUID,
  name: String,
  description: Option[String],
  imageUrl: String,
  parentId: Option[UUID],
  restaurantId: Option[UUID],
  children: List[CategoryDto],
)

object CategoryDto {
  // Hand-written codecs: `children` makes this type recursive, which circe's
  // generic derivation cannot handle without blowing up inline expansion.
  given encoder: Encoder[CategoryDto] = new Encoder[CategoryDto] {
    override def apply(category: CategoryDto): Json =
      Json.obj(
        "uuid" -> category.uuid.asJson,
        "name" -> category.name.asJson,
        "description" -> category.description.asJson,
        "imageUrl" -> category.imageUrl.asJson,
        "parentId" -> category.parentId.asJson,
        "restaurantId" -> category.restaurantId.asJson,
        "children" -> Json.fromValues(category.children.map(apply)),
      )
  }

  given decoder: Decoder[CategoryDto] = new Decoder[CategoryDto] {
    override def apply(cursor: HCursor): Decoder.Result[CategoryDto] =
      for {
        uuid <- cursor.get[UUID]("uuid")
        name <- cursor.get[String]("name")
        description <- cursor.get[Option[String]]("description")
        imageUrl <- cursor.get[String]("imageUrl")
        parentId <- cursor.get[Option[UUID]]("parentId")
        restaurantId <- cursor.get[Option[UUID]]("restaurantId")
        children <- cursor.get[List[CategoryDto]]("children")
      } yield CategoryDto(uuid, name, description, imageUrl, parentId, restaurantId, children)
  }
}
