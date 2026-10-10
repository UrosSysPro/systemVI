package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.{AuthIdentity, AuthProvider}

import java.util.UUID

class AuthIdentityContextTest extends CatsEffectSuite {

  private def withIdentities[A](f: AuthIdentityContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(AuthIdentityContext.create[IO]).use(f)

  private def sample(userId: UUID, subject: String = UUID.randomUUID().toString): AuthIdentity =
    AuthIdentity(
      uuid = UUID.randomUUID(),
      userId = userId,
      provider = AuthProvider.Google,
      providerSubject = subject,
      email = Some("someone@example.com"),
    )

  test("add persists an identity that can then be fetched by id") {
    val identity = sample(UUID.randomUUID())
    withIdentities { identities =>
      for {
        inserted <- identities.add(identity)
        fetched <- identities.get(identity.uuid)
      } yield {
        assertEquals(inserted, 1)
        assertEquals(fetched, Some(identity))
      }
    }
  }

  test("get returns None for an unknown id") {
    withIdentities { identities =>
      identities.get(UUID.randomUUID()).map(fetched => assertEquals(fetched, None))
    }
  }

  test("getByProviderSubject finds the identity for a given provider handle") {
    val identity = sample(UUID.randomUUID(), "google-subject-1")
    withIdentities { identities =>
      for {
        _ <- identities.add(identity)
        fetched <- identities.getByProviderSubject(AuthProvider.Google, "google-subject-1")
        missing <- identities.getByProviderSubject(AuthProvider.Google, "google-subject-2")
      } yield {
        assertEquals(fetched, Some(identity))
        assertEquals(missing, None)
      }
    }
  }

  test("getByUser returns every identity linked to that user") {
    val userId = UUID.randomUUID()
    val first = sample(userId, "google-subject-1")
    val second = sample(userId, "google-subject-2")
    val foreign = sample(UUID.randomUUID(), "google-subject-3")
    withIdentities { identities =>
      for {
        _ <- identities.add(first)
        _ <- identities.add(second)
        _ <- identities.add(foreign)
        result <- identities.getByUser(userId)
      } yield assertEquals(result.toSet, Set(first, second))
    }
  }

  test("delete removes a persisted identity") {
    val identity = sample(UUID.randomUUID())
    withIdentities { identities =>
      for {
        _ <- identities.add(identity)
        deleted <- identities.delete(identity.uuid)
        fetched <- identities.get(identity.uuid)
      } yield {
        assertEquals(deleted, 1)
        assertEquals(fetched, None)
      }
    }
  }

  test("delete affects no rows for an unknown identity") {
    withIdentities { identities =>
      identities.delete(UUID.randomUUID()).map(deleted => assertEquals(deleted, 0))
    }
  }
}
