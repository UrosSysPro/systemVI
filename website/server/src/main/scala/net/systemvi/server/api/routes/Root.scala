package net.systemvi.server.api.routes

import cats.*
import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import net.systemvi.server.api.*
import net.systemvi.server.api.controllers.*
import net.systemvi.server.persistance.contexts.ApplicationContext
import net.systemvi.server.persistance.database.*
import net.systemvi.server.persistance.migrations.Migrations
import org.http4s.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.typelevel.log4cats.slf4j.Slf4jLogger

def router(using context:ApplicationContext[IO]) = Router(
  "/api/manufacturers" -> manufacturerController,
  "/api/switches" -> switchController,
  "/api/keyboards" -> keyboardController,
  "/api/applications" -> applicationController,
  "/" -> websiteController
).orNotFound
