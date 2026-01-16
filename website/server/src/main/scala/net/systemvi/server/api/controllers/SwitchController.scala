package net.systemvi.server.api.controllers

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
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


def switchController(using context: ApplicationContext[IO]) = HttpRoutes.of[IO]{
  case GET -> Root => ???
  case GET -> Root / UUIDVar(id) => ???
}
