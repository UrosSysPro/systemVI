package net.systemvi.mainmenu.server.services

import net.systemvi.mainmenu.server.config.Config
import org.http4s.*
import org.http4s.implicits.*

/**
 * Google OAuth endpoints, and the callback URI that must be registered for the
 * OAuth client.
 *
 * The callback is derived from `ServerConfig.serverUrl` so it always matches the
 * route the server actually serves (`/api/auth/google/callback`); whatever host
 * and port the server runs on is what you register with Google.
 */
final class GoogleUriService(config: Config) {
  private val scopes = List(
    "openid",
    "https://www.googleapis.com/auth/userinfo.email",
    "https://www.googleapis.com/auth/userinfo.profile",
  )

  /** The OAuth redirect URI. Must be registered, and must be sent again on token exchange. */
  val callbackUri: Uri =
    config.server.serverUrl / "api" / "auth" / "google" / "callback"

  /** The page we send the browser to so the user can grant access. */
  val loginRedirectUri: Uri =
    Uri
      .unsafeFromString("https://accounts.google.com/o/oauth2/v2/auth")
      .withQueryParam("client_id", config.googleAuth.clientId)
      .withQueryParam("redirect_uri", callbackUri.renderString)
      .withQueryParam("response_type", "code")
      .withQueryParam("scope", scopes.mkString(" "))
      .withQueryParam("access_type", "online")
      .withQueryParam("include_granted_scopes", "true")
      .withQueryParam("state", "login")

  /** Exchanges the authorization code for an access token (POST, form-encoded). */
  val accessTokenUri: Uri = uri"https://oauth2.googleapis.com/token"

  /** Returns the signed-in user's profile for a bearer access token. */
  val userProfileUri: Uri = uri"https://openidconnect.googleapis.com/v1/userinfo"
}

object GoogleUriService {
  def apply(config: Config): GoogleUriService = new GoogleUriService(config)
}
