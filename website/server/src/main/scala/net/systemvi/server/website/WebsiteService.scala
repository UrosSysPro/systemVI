package net.systemvi.server.website

import cats.*
import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import net.systemvi.server.utils.getLogger
import fs2.io.file.Path

val websiteService= HttpRoutes.of[IO] {
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