package net.systemvi.mainmenu.server

import cats.effect.*
import ciris.Secret
import com.comcast.ip4s.{Host, Port}
import doobie.*
import net.systemvi.mainmenu.server.config.*
import net.systemvi.mainmenu.server.persistance.contexts.*
import net.systemvi.mainmenu.server.services.*
import org.http4s.*
import org.http4s.client.Client
import org.typelevel.log4cats.slf4j.Slf4jLogger

/** Builds an `AppContext` for tests, with a client that never touches the network. */
object TestAppContext {

  val config: Config = Config(
    ServerConfig(
      Host.fromString("0.0.0.0").get,
      Port.fromString("8080").get,
      Uri.unsafeFromString("http://localhost:8080"),
      Uri.unsafeFromString("http://localhost:5174"),
    ),
    GoogleAuthConfig("test-client-id", Secret("test-client-secret")),
    JwtAuthConfig("test-jwt-secret"),
  )

  def client: Client[IO] = Client.fromHttpApp[IO](HttpApp.notFound[IO])

  /** A Google API service backed by a client that never reaches Google. */
  lazy val googleApi: GoogleApiService[IO] =
    GoogleApiService.make(client, config, GoogleUriService(config))

  def create(xa: Transactor[IO], googleApiService: GoogleApiService[IO] = googleApi): AppContext[IO] = {
    val uris = GoogleUriService(config)
    AppContext(
      xa = xa,
      db = DatabaseContext.create(xa),
      logger = Slf4jLogger.getLogger[IO],
      config = config,
      httpClient = client,
      googleUriService = uris,
      googleApiService = googleApiService,
      jwtService = JwtService(config.jwtAuth),
    )
  }
}
