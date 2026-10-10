package net.systemvi.mainmenu.server.api.routes

import cats.effect.*
import doobie.*
import io.circe.syntax.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.common.dto.UserDto
import net.systemvi.mainmenu.server.{TestAppContext, TestDatabase}
import net.systemvi.mainmenu.server.persistance.contexts.{AppContext, AuthIdentityContext, UserContext}
import net.systemvi.mainmenu.server.persistance.models.{AuthProvider, Role, UserStatus}
import net.systemvi.mainmenu.server.services.{GoogleApiService, GoogleTokenResponse, GoogleUserProfile}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.headers.{Cookie, Location}
import org.http4s.implicits.*

class GoogleAuthFlowTest extends CatsEffectSuite {

  private val alice = GoogleUserProfile(
    sub = "google-sub-1",
    email = "alice@example.com",
    email_verified = true,
    name = Some("Alice"),
    picture = None,
    given_name = None,
    family_name = None,
    locale = None,
  )

  private def fakeGoogleApi(profile: GoogleUserProfile): GoogleApiService[IO] =
    new GoogleApiService[IO] {
      override def getAccessToken(code: String): IO[GoogleTokenResponse] =
        IO.pure(GoogleTokenResponse("access-token", 3600, "scope", "Bearer"))
      override def getUserProfile(accessToken: String): IO[GoogleUserProfile] =
        IO.pure(profile)
    }

  private def withRoutes[A](googleApi: GoogleApiService[IO])(
    f: (Transactor[IO], HttpRoutes[IO], AppContext[IO]) => IO[A]
  ): IO[A] =
    TestDatabase.transactor.use { xa =>
      val context = TestAppContext.create(xa, googleApi)
      f(xa, api(using context), context)
    }

  test("redirect sends the browser to Google") {
    withRoutes(fakeGoogleApi(alice)) { (_, routes, _) =>
      routes.orNotFound
        .run(Request[IO](Method.GET, uri"/auth/google/redirect"))
        .map { response =>
          assertEquals(response.status, Status.Found)
          assert(
            response.headers.get[Location].exists(_.uri.host.exists(_.value == "accounts.google.com"))
          )
        }
    }
  }

  test("callback creates a customer identity, sets the session cookie and redirects to the client") {
    withRoutes(fakeGoogleApi(alice)) { (xa, routes, _) =>
      val users = UserContext.create[IO](xa)
      val identities = AuthIdentityContext.create[IO](xa)
      for {
        response <- routes.orNotFound.run(
          Request[IO](Method.GET, uri"/auth/google/callback?code=abc")
        )
        allUsers <- users.getAll()
        user = allUsers.head
        linked <- identities.getByUser(user.uuid)
      } yield {
        assertEquals(response.status, Status.Found)
        assertEquals(
          response.headers.get[Location].map(_.uri.renderString),
          Some("http://localhost:5174/welcome"),
        )
        assert(response.cookies.exists(_.name == "access_token"), response.cookies)

        assertEquals(allUsers.size, 1)
        assertEquals(user.role, Role.Customer)
        assertEquals(user.status, UserStatus.Active)
        assertEquals(user.name, Some("Alice"))
        assertEquals(user.email, Some("alice@example.com"))

        assertEquals(linked.map(_.provider), List(AuthProvider.Google))
        assertEquals(linked.map(_.providerSubject), List("google-sub-1"))
      }
    }
  }

  test("a second sign-in for the same google subject reuses the existing user") {
    withRoutes(fakeGoogleApi(alice)) { (xa, routes, _) =>
      for {
        _ <- routes.orNotFound.run(Request[IO](Method.GET, uri"/auth/google/callback?code=one"))
        _ <- routes.orNotFound.run(Request[IO](Method.GET, uri"/auth/google/callback?code=two"))
        allUsers <- UserContext.create[IO](xa).getAll()
      } yield assertEquals(allUsers.size, 1)
    }
  }

  test("callback without a code is a bad request") {
    withRoutes(fakeGoogleApi(alice)) { (_, routes, _) =>
      routes.orNotFound
        .run(Request[IO](Method.GET, uri"/auth/google/callback"))
        .map(response => assertEquals(response.status, Status.BadRequest))
    }
  }

  test("a Google failure is reported as a server error") {
    val failing = new GoogleApiService[IO] {
      override def getAccessToken(code: String): IO[GoogleTokenResponse] =
        IO.raiseError(new RuntimeException("boom"))
      override def getUserProfile(accessToken: String): IO[GoogleUserProfile] =
        IO.raiseError(new RuntimeException("boom"))
    }
    withRoutes(failing) { (_, routes, _) =>
      routes.orNotFound
        .run(Request[IO](Method.GET, uri"/auth/google/callback?code=abc"))
        .map(response => assertEquals(response.status, Status.InternalServerError))
    }
  }

  test("the session cookie authorises /user and it returns the signed-in user") {
    withRoutes(fakeGoogleApi(alice)) { (xa, routes, context) =>
      for {
        _ <- routes.orNotFound.run(Request[IO](Method.GET, uri"/auth/google/callback?code=abc"))
        user <- UserContext.create[IO](xa).getAll().map(_.head)
        session = UserDto(user.uuid, user.role.name, user.status.name, user.name, user.email)
        token = context.jwtService.encode(session.asJson.noSpaces)
        response <- routes.orNotFound.run(
          Request[IO](Method.GET, uri"/user").putHeaders(Cookie(RequestCookie("access_token", token)))
        )
        body <- response.as[UserDto]
      } yield {
        assertEquals(response.status, Status.Ok)
        assertEquals(body, session)
      }
    }
  }

  test("/user without a session cookie is forbidden") {
    withRoutes(fakeGoogleApi(alice)) { (_, routes, _) =>
      routes.orNotFound
        .run(Request[IO](Method.GET, uri"/user"))
        .map(response => assertEquals(response.status, Status.Forbidden))
    }
  }
}
