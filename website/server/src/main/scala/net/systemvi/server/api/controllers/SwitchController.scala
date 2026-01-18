package net.systemvi.server.api.controllers

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
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
  case GET -> Root => for{
    switch <- context.db.switches.get() 
    response <- Ok(switch.asJson)
  } yield response
  
  case GET -> Root / UUIDVar(id) => for{
    switch <- context.db.switches.get(id)
    response <- Ok(switch.asJson)
  } yield response
}
