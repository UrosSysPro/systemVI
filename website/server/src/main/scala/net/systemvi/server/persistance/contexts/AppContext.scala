package net.systemvi.server.persistance.contexts

import cats.effect.IO
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class AppContext[F[_]](
                             db:DatabaseContext[F],
                             logger:Logger[IO]
                             )

object AppContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]):AppContext[F]= {
    AppContext(
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[IO]
    )
  }
}

