package net.systemvi.server.api

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.typelevel.log4cats.slf4j.Slf4jLogger

val apiService=HttpRoutes.of[IO]{
  case _ => for{
    logger<-IO(Slf4jLogger.getLoggerFromName[IO]("app"))
    _<-logger.info("api request called")
    _<-logger.debug("api request called")
    _<-logger.warn("api request called")
    _<-logger.error("api request called")
    response <- IO(Response[IO](Status.Ok))
  }yield response
}