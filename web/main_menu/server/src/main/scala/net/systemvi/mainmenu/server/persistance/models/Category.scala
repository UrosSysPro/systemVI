package net.systemvi.mainmenu.server.persistance.models

import java.util.UUID

case class Category(
  uuid: UUID,
  name: String,
  description: Option[String],
  imageUrl: String,
  parentId: Option[UUID],
  restaurantId: Option[UUID],
  /**
   * The uploaded image, if any. Kept out of the `Read[Category]` mapping and so
   * always `None` on entities that come back from a query: image bytes must
   * never travel with a listing, or every menu fetch would drag the whole album
   * along. The context has dedicated blob-only methods instead.
   */
  imageBlob: Option[Array[Byte]] = None,
)
