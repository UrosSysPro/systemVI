package net.systemvi.server.api.controllers

import cats.*
import cats.effect.*
import fs2.io.file.Path
import org.http4s.*
import org.http4s.dsl.io.*

val websiteController = HttpRoutes.of[IO] {
  case request =>
    val websiteFile = StaticFile.fromPath(
      Path(s"./public/dist${request.uri.path.toString}"),
      Some(request)
    )
    val publicFile = StaticFile.fromPath(
      Path(s"./public/${request.uri.path.toString}"),
      Some(request)
    )
    val indexFile = StaticFile.fromPath(
      Path("./public/dist/index.html"),
      Some(request)
    )
    for {
      response <- publicFile
        .orElse(websiteFile)
        .orElse(indexFile)
        .getOrElseF(NotFound())
    } yield response
}