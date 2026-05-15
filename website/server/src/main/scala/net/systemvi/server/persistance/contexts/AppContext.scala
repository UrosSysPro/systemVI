package net.systemvi.server.persistance.contexts

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.implicits.*
import doobie.*
import doobie.implicits.*
import net.systemvi.server.config.Config
import net.systemvi.server.services.*
import net.systemvi.server.persistance.database.*
import org.http4s.client.*
import org.http4s.ember.client.*
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.*

case class AppContext[F[_]](
                             xa: Transactor[F],
                             db: DatabaseContext[F],
                             logger: Logger[F],
                             config: Config,
                             httpClient: Client[F],
                             googleUriService: GoogleUriService[F],
                             googleApiService: GoogleApiService[F]
                           )

object AppContext {
  def create[F[_]: MonadCancelThrow: Async: Sync]: Resource[F, AppContext[F]] = {
    for{
      xa <- sqlite[F]
      httpClient <- EmberClientBuilder.default[F].build
      googleApiService <- Resource.eval[F,GoogleApiService[F]](Async[F].delay{GoogleApiService.make[F]})
      googleUriService <- Resource.eval[F,GoogleUriService[F]](Async[F].delay{GoogleUriService.make[F]})
      config <- Resource.eval(Config.instance.load[F])
    } yield AppContext(
      xa = xa,
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[F],
      config = config,
      httpClient = httpClient,
      googleUriService = googleUriService,
      googleApiService = googleApiService,
    )
  }
}

