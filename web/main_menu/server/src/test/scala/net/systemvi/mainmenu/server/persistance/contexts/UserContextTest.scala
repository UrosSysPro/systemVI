package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.{Role, User, UserStatus}

import java.util.UUID

class UserContextTest extends CatsEffectSuite {

  private def withUsers[A](f: UserContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(UserContext.create[IO]).use(f)

  private def anonymous(email: Option[String] = None): User =
    User(
      uuid = UUID.randomUUID(),
      role = Role.Customer,
      status = UserStatus.Anonymous,
      name = None,
      email = email,
    )

  test("add persists a user that can then be fetched by id") {
    val user = anonymous()
    withUsers { users =>
      for {
        inserted <- users.add(user)
        fetched <- users.get(user.uuid)
      } yield {
        assertEquals(inserted, 1)
        assertEquals(fetched, Some(user))
      }
    }
  }

  test("get returns None for an unknown id") {
    withUsers { users =>
      users.get(UUID.randomUUID()).map(fetched => assertEquals(fetched, None))
    }
  }

  test("getAll returns every persisted user") {
    val a = anonymous()
    val b = anonymous()
    withUsers { users =>
      for {
        _ <- users.add(a)
        _ <- users.add(b)
        all <- users.getAll()
      } yield assertEquals(all.toSet, Set(a, b))
    }
  }

  test("getByEmail finds the user with that email") {
    val user = anonymous(Some("waiter@example.com"))
    withUsers { users =>
      for {
        _ <- users.add(user)
        fetched <- users.getByEmail("waiter@example.com")
        missing <- users.getByEmail("nobody@example.com")
      } yield {
        assertEquals(fetched, Some(user))
        assertEquals(missing, None)
      }
    }
  }

  test("update replaces every field") {
    val user = anonymous()
    val updated = user.copy(
      role = Role.Waiter,
      status = UserStatus.Active,
      name = Some("Alice"),
      email = Some("alice@example.com"),
    )
    withUsers { users =>
      for {
        _ <- users.add(user)
        affected <- users.update(updated)
        fetched <- users.get(user.uuid)
      } yield {
        assertEquals(affected, 1)
        assertEquals(fetched, Some(updated))
      }
    }
  }

  test("update affects no rows for an unknown user") {
    withUsers { users =>
      users.update(anonymous()).map(affected => assertEquals(affected, 0))
    }
  }

  test("delete removes a persisted user") {
    val user = anonymous()
    withUsers { users =>
      for {
        _ <- users.add(user)
        deleted <- users.delete(user.uuid)
        fetched <- users.get(user.uuid)
      } yield {
        assertEquals(deleted, 1)
        assertEquals(fetched, None)
      }
    }
  }

  test("delete affects no rows for an unknown user") {
    withUsers { users =>
      users.delete(UUID.randomUUID()).map(deleted => assertEquals(deleted, 0))
    }
  }
}
