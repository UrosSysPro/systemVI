package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import doobie.*
import fs2.io.net.Network
import net.systemvi.mainmenu.server.config.Config
import net.systemvi.mainmenu.server.persistance.database.sqlite
import net.systemvi.mainmenu.server.services.{GoogleApiService, GoogleUriService, JwtService}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.*

case class AppContext[F[_]](
  xa: Transactor[F],
  db: DatabaseContext[F],
  logger: Logger[F],
  config: Config,
  httpClient: Client[F],
  googleUriService: GoogleUriService,
  googleApiService: GoogleApiService[F],
  jwtService: JwtService,
)

object AppContext {
  def create[F[_]: MonadCancelThrow: Async: Sync]: Resource[F, AppContext[F]] = {
    given Network[F] = Network.forAsync[F]
    for {
      xa <- sqlite[F]
      httpClient <- EmberClientBuilder.default[F].build
      config <- Resource.eval(Config.instance.load[F])
      googleUriService = GoogleUriService(config)
      jwtService = JwtService(config.jwtAuth)
    } yield AppContext(
      xa = xa,
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[F],
      config = config,
      httpClient = httpClient,
      googleUriService = googleUriService,
      googleApiService = GoogleApiService.make(httpClient, config, googleUriService),
      jwtService = jwtService,
    )
  }
}
