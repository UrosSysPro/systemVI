package net.systemvi.mainmenu.server.api.controllers

import cats.effect.*
import net.systemvi.mainmenu.server.persistance.contexts.AppContext
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`

import java.util.UUID

/**
 * Serves stored images and accepts new ones.
 *
 * Images live in the database as blobs (see
 * [[net.systemvi.mainmenu.server.persistance.migrations.__12_alter_categories_and_items_add_imageBlob_column]]),
 * and these routes are the only way in or out:
 *
 *  - `GET /image/category/{id}` and `GET /image/item/{id}` return the bytes.
 *  - `PUT /category/{id}/image` and `PUT /items/{id}/image` (in the controllers
 *    that own those paths) store them.
 *
 * The GET routes are public: menu images are shown to anonymous customers, so
 * requiring a session here would break the read-only menu.
 */
def imageController(using context: AppContext[IO]): HttpRoutes[IO] = {
  val logger = context.logger

  def serve(bytes: Option[Array[Byte]]): IO[Response[IO]] =
    bytes match {
      case Some(image) if image.nonEmpty =>
        Ok(image).map(_.withContentType(`Content-Type`(MediaType.image.png)))
      case _ =>
        NotFound()
    }

  HttpRoutes.of[IO] {
    case GET -> Root / "category" / UUIDVar(id) =>
      context.db.categories.getImage(id).flatMap(serve)

    case GET -> Root / "item" / UUIDVar(id) =>
      context.db.items.getImage(id).flatMap(serve)
  }
}

/**
 * Reads the raw request body as image bytes.
 *
 * `maxBytes` caps the upload so a large body cannot exhaust the SQLite blob;
 * anything bigger is rejected with a message the client can show.
 */
def readImageBody(request: Request[IO], maxBytes: Long = 5L * 1024 * 1024): IO[Either[String, Array[Byte]]] =
  request.body.compile.to(Array).map { bytes =>
    if bytes.isEmpty then Left("The uploaded file is empty.")
    else if bytes.length > maxBytes then Left(s"The image is too large (limit ${maxBytes / 1024 / 1024} MB).")
    else Right(bytes)
  }
