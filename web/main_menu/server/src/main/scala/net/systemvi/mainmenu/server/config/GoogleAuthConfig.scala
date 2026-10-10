package net.systemvi.mainmenu.server.config

import ciris.*
import ciris.circe.circeConfigDecoder
import io.circe.*

import java.nio.file.Paths

/**
 * Google OAuth client credentials, read from the JSON file that Google issues
 * for an OAuth client (`config/google-client-secret.json`).
 *
 * Only `client_id` / `client_secret` are used; the other fields in that file are
 * ignored. The callback URI is derived from `ServerConfig.serverUrl` (see
 * `GoogleUriService`), so there is a single place to keep it in sync with what
 * is registered in the Google Cloud console.
 */
case class GoogleAuthConfig(
  clientId: String,
  clientSecret: Secret[String],
)

object GoogleAuthConfig {
  given Decoder[GoogleAuthConfig] = Decoder.instance { cursor =>
    val web = cursor.downField("web")
    for {
      clientId <- web.get[String]("client_id")
      clientSecret <- web.get[String]("client_secret")
    } yield GoogleAuthConfig(clientId, Secret(clientSecret))
  }

  given ConfigDecoder[String, GoogleAuthConfig] = circeConfigDecoder("GoogleAuthConfigDecoder")

  def instance: ConfigValue[Effect, GoogleAuthConfig] =
    file(Paths.get("config/google-client-secret.json")).as[GoogleAuthConfig]
}
