package net.systemvi.server.persistance.contexts

import cats.effect.IO
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import net.systemvi.server.config.Config
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class AppContext[F[_]](
                             db: DatabaseContext[F],
                             logger: Logger[IO],
                             config: Config
                           )

object AppContext {
  def create[F[_]: MonadCancelThrow](xa: Transactor[F], config: Config): AppContext[F]= {
    AppContext(
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[IO],
      config = config
    )
  }
}

