package net.systemvi.server.utils

import org.typelevel.log4cats.slf4j.Slf4jLogger
import cats.effect.IO

def getLogger=IO{Slf4jLogger.getLoggerFromName[IO]("app")}