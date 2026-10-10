package net.systemvi.mainmenu.server.services

import munit.FunSuite
import net.systemvi.mainmenu.server.TestAppContext

class GoogleUriServiceTest extends FunSuite {

  private val uris = GoogleUriService(TestAppContext.config)

  test("the callback uri is derived from the server url") {
    assertEquals(uris.callbackUri.renderString, "http://localhost:8080/api/auth/google/callback")
  }

  test("the login redirect points at Google with the registered callback and requested scopes") {
    val redirect = uris.loginRedirectUri
    val params = redirect.multiParams

    assertEquals(redirect.host.map(_.value), Some("accounts.google.com"))
    assertEquals(redirect.path.renderString, "/o/oauth2/v2/auth")
    assertEquals(params.get("client_id").flatMap(_.headOption), Some("test-client-id"))
    assertEquals(
      params.get("redirect_uri").flatMap(_.headOption),
      Some("http://localhost:8080/api/auth/google/callback"),
    )
    assertEquals(params.get("response_type").flatMap(_.headOption), Some("code"))
    assertEquals(params.get("client_secret"), None)

    val scope = params.get("scope").flatMap(_.headOption).getOrElse("")
    assert(scope.contains("openid"), scope)
    assert(scope.contains("userinfo.email"), scope)
    assert(scope.contains("userinfo.profile"), scope)
  }

  test("the token exchange and userinfo endpoints are the Google ones") {
    assertEquals(uris.accessTokenUri.renderString, "https://oauth2.googleapis.com/token")
    assertEquals(uris.userProfileUri.renderString, "https://openidconnect.googleapis.com/v1/userinfo")
  }
}
