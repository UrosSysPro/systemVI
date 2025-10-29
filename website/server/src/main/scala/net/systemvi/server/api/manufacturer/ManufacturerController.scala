package net.systemvi.server.api.manufacturer

import cats.*
import cats.implicits.*
import cats.effect.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import io.circe.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import net.systemvi.server.services.*
import net.systemvi.common.model.*
import net.systemvi.server.db.manufacturer.ManufacturerDB
import fs2.*

val manufacturerRoutes=HttpRoutes.of[IO]{

  case GET -> Root  => ManufacturerDB.getAll().flatMap{list=>Ok(list.asJson)}

  case GET -> Root / IntVar(id) => ManufacturerDB.get(id).flatMap{
    case Some(value) => Ok(value.asJson)
    case None => NotFound()
  }

  case request@POST -> Root => for{
    body <- request.body.fold("")((acc,b)=>acc+b.toChar).compile.toList
    response <- Ok(body.foldLeft(""){(acc,b)=>acc+b})
  }yield response

  case PUT -> Root => Ok("update manufacturer")
  case DELETE -> Root => Ok("delete manufacturer")
}
