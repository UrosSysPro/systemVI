package net.systemvi.website.routes

import cats.*
import cats.implicits.*
import io.circe.*
import net.systemvi.website.*
import urldsl.errors.DummyError
import urldsl.vocabulary.{FromString, Printer}
import net.systemvi.website.routes.Pages.*
import java.util.UUID
import scala.util.*

object PageDecoders {
  //  Encode and decode UUID in url
  given FromString[UUID, DummyError] = (str: String) => {
    Try {
      UUID.fromString(str)
    } match {
      case Success(value) => value.asRight
      case Failure(exception) => Left[DummyError, UUID](DummyError.dummyError)
    }
  }

  given Printer[UUID] = (t: UUID) => t.toString

  // Decode Pages from json in history api
  given Decoder[HomePage.type] = cursor => for {
    _ <- cursor.get[Json]("HomePage")
  } yield HomePage

  given Decoder[GamesPage.type] = cursor => for {
    _ <- cursor.get[Json]("GamesPage")
  } yield GamesPage

  given Decoder[KeyboardsPage.type] = cursor => for {
    _ <- cursor.get[Json]("KeyboardsPage")
  } yield KeyboardsPage

  given Decoder[EnginePage.type] = cursor => for {
    _ <- cursor.get[Json]("EnginePage")
  } yield EnginePage

  given Decoder[NotFoundPage.type] = cursor => for {
    _ <- cursor.get[Json]("NotFoundPage")
  } yield NotFoundPage

  given Decoder[GamePage] = cursor => for {
    child <- cursor.get[Json]("GamePage")
    id <- child.hcursor.get[Int]("gameId")
  } yield GamePage(id)

  given Decoder[KeyboardPage] = cursor => for {
    child <- cursor.get[Json]("KeyboardPage")
    uuid <- child.hcursor.get[UUID]("keyboardId")
  } yield KeyboardPage(uuid)

  given Decoder[ApplicationDetailsPage] = cursor => for {
    child <- cursor.get[Json]("ApplicationDetailsPage")
    uuid <- child.hcursor.get[UUID]("applicationUUID")
  } yield ApplicationDetailsPage(uuid)

  given Decoder[ConfiguratorPage.type] = cursor => for {
    _ <- cursor.get[Json]("ConfiguratorPage")
  } yield ConfiguratorPage

  given Decoder[ThreeDPrintingPage.type] = cursor => for {
    _ <- cursor.get[Json]("ThreeDPrintingPage")
  } yield ThreeDPrintingPage

  given Decoder[OrigamiPage.type] = cursor => for {
    _ <- cursor.get[Json]("OrigamiPage")
  } yield OrigamiPage

  given Decoder[KnittingPage.type] = cursor => for {
    _ <- cursor.get[Json]("KnittingPage")
  } yield KnittingPage
}