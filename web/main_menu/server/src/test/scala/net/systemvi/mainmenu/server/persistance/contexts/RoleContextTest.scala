package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.Role

class RoleContextTest extends CatsEffectSuite {

  private def withRoles[A](f: RoleContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(RoleContext.create[IO]).use(f)

  test("the migration seeds all three roles") {
    withRoles { roles =>
      roles.getAll().map(all => assertEquals(all, Role.all))
    }
  }

  test("get returns the role for a known id") {
    withRoles { roles =>
      roles.get(Role.Admin.id).map(fetched => assertEquals(fetched, Some(Role.Admin)))
    }
  }

  test("get returns None for an unknown id") {
    withRoles { roles =>
      roles.get(999).map(fetched => assertEquals(fetched, None))
    }
  }

  test("getByName returns the role for a known name") {
    withRoles { roles =>
      roles.getByName("WAITER").map(fetched => assertEquals(fetched, Some(Role.Waiter)))
    }
  }

  test("getByName returns None for an unknown name") {
    withRoles { roles =>
      roles.getByName("SUPERUSER").map(fetched => assertEquals(fetched, None))
    }
  }
}
