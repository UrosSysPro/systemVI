package net.systemvi.mainmenu.server.persistance.contexts

import cats.*
import cats.effect.*
import doobie.*
import net.systemvi.mainmenu.server.config.Config
import net.systemvi.mainmenu.server.persistance.database.sqlite
import org.typelevel.log4cats.*
import org.typelevel.log4cats.slf4j.*

case class AppContext[F[_]](
  xa: Transactor[F],
  db: DatabaseContext[F],
  logger: Logger[F],
  config: Config,
)

object AppContext {
  def create[F[_]: MonadCancelThrow: Async: Sync]: Resource[F, AppContext[F]] =
    for {
      xa <- sqlite[F]
      config <- Resource.eval(Config.instance.load[F])
    } yield AppContext(
      xa = xa,
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[F],
      config = config,
    )
}
