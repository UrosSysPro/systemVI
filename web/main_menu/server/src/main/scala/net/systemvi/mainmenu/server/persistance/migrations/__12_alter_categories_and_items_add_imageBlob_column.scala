package net.systemvi.mainmenu.server.persistance.migrations

import cats.effect.*
import cats.syntax.all.*
import doobie.*

/**
 * Adds the `imageBlob` column to `Categories` and `Items`.
 *
 * Images are stored in the database rather than on disk: the app has no upload
 * storage, one process owns the SQLite file, and menus are small enough that a
 * few low-resolution images are not a problem. The proxy route
 * (`/image/category/{id}`, `/image/item/{id}`) reads these blobs back out.
 *
 * `imageUrl` is deliberately kept. It still holds the URL the client renders,
 * which the controllers point at the proxy route for uploaded images, so views
 * that do `img(src := imageUrl)` need no special casing.
 */
class __12_alter_categories_and_items_add_imageBlob_column[F[_]: MonadCancelThrow](xa: Transactor[F])
    extends BaseMigration[F](xa) {

  override def up: F[Int] =
    for {
      categories <- addColumn("Categories", "imageBlob", "BLOB null")
      items <- addColumn("Items", "imageBlob", "BLOB null")
    } yield categories + items

  override def down: F[Int] =
    for {
      categories <- dropColumn("Categories", "imageBlob")
      items <- dropColumn("Items", "imageBlob")
    } yield categories + items
}
