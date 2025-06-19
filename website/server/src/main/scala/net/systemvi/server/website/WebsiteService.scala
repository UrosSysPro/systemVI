package net.systemvi.server.website

import cats.*
import cats.data.Kleisli
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import net.systemvi.server.services.{GameService, KeyboardService}
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