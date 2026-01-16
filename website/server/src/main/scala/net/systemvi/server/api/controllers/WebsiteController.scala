package net.systemvi.server.api.controllers

import cats.*
import cats.effect.*
import fs2.io.file.Path
import net.systemvi.server.utils.getLogger
import org.http4s.*
import org.http4s.dsl.io.*

val websiteController = HttpRoutes.of[IO] {
  case request => for{
    logger<-getLogger

    websiteFile=StaticFile.fromPath(
      Path(s"./public/dist${request.uri.path.toString}"),
      Some(request)
    )
    publicFile=StaticFile.fromPath(
      Path(s"./public/${request.uri.path.toString}"),
      Some(request)
    )
    indexFile=StaticFile.fromPath(
      Path("./public/dist/index.html"),
      Some(request)
    )

    response<-publicFile
      .orElse(websiteFile)
      .orElse(indexFile)
      .getOrElseF(NotFound())
  } yield response
}