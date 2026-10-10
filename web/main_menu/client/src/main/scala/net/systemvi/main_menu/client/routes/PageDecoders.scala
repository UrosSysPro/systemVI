package net.systemvi.main_menu.client.routes

import cats.*
import cats.implicits.*
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}

import java.util.UUID
import scala.util.*

object PageDecoders {
  // URL DSL needs a way to (de)serialise our route parameters.
  given FromString[UUID, DummyError] = (str: String) =>
    Try(UUID.fromString(str)) match {
      case Success(value) => value.asRight
      case Failure(_) => Left(DummyError.dummyError)
    }

  given Printer[UUID] = (uuid: UUID) => uuid.toString
}
