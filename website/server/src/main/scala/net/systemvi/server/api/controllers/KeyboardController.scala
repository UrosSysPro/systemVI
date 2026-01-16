package net.systemvi.server.api.controllers


import cats.*
import cats.effect.*
import cats.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import fs2.*
import net.systemvi.server.persistance.contexts.ApplicationContext
import net.systemvi.server.persistance.models.Manufacturer
import net.systemvi.server.services.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*

def keyboardController(using context: ApplicationContext[IO]) = HttpRoutes.of[IO] {
  case GET -> Root => ???
  case GET -> Root / UUIDVar(id) => ???
}
