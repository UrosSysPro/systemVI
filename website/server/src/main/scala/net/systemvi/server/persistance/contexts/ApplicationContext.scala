package net.systemvi.server.persistance.contexts

import cats.effect.IO
import cats.effect.kernel.MonadCancelThrow
import doobie.util.transactor.Transactor
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class ApplicationContext[F[_]](
                             db:DatabaseContext[F],
                             logger:Logger[IO]
                             )

object ApplicationContext {
  def create[F[_]:MonadCancelThrow](xa:Transactor[F]):ApplicationContext[F]= {
    ApplicationContext(
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[IO]
    )
  }
}

