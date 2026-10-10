package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.RestaurantTable

import java.util.UUID

class RestaurantTableContextTest extends CatsEffectSuite {

  private def withTables[A](f: RestaurantTableContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(RestaurantTableContext.create[IO]).use(f)

  private def sampleTable(restaurantId: UUID, name: String = "Table 1"): RestaurantTable =
    RestaurantTable(
      uuid = UUID.randomUUID(),
      name = name,
      imageUrl = s"https://cdn.example.com/${name.toLowerCase.replace(' ', '-')}.png",
      restaurantId = restaurantId,
    )

  test("add persists a table that can then be fetched by id") {
    val restaurantId = UUID.randomUUID()
    val table = sampleTable(restaurantId)
    withTables { tables =>
      for {
        inserted <- tables.add(table)
        fetched <- tables.get(table.uuid)
      } yield {
        assertEquals(inserted, 1)
        assertEquals(fetched, Some(table))
      }
    }
  }

  test("get returns None for an unknown id") {
    withTables { tables =>
      tables.get(UUID.randomUUID()).map(fetched => assertEquals(fetched, None))
    }
  }

  test("getAll returns every persisted table") {
    val restaurantId = UUID.randomUUID()
    val a = sampleTable(restaurantId, "Table 1")
    val b = sampleTable(restaurantId, "Table 2")
    withTables { tables =>
      for {
        _ <- tables.add(a)
        _ <- tables.add(b)
        all <- tables.getAll()
      } yield assertEquals(all.toSet, Set(a, b))
    }
  }

  test("getByRestaurant returns only that restaurant's tables") {
    val restaurantA = UUID.randomUUID()
    val restaurantB = UUID.randomUUID()
    val first = sampleTable(restaurantA, "Table 1")
    val second = sampleTable(restaurantA, "Table 2")
    val foreign = sampleTable(restaurantB, "Table 1")
    withTables { tables =>
      for {
        _ <- tables.add(first)
        _ <- tables.add(second)
        _ <- tables.add(foreign)
        result <- tables.getByRestaurant(restaurantA)
      } yield assertEquals(result.toSet, Set(first, second))
    }
  }

  test("update replaces every field, including the restaurant") {
    val firstRestaurant = UUID.randomUUID()
    val secondRestaurant = UUID.randomUUID()
    val table = sampleTable(firstRestaurant)
    val updated = table.copy(
      name = "Patio 3",
      imageUrl = "https://cdn.example.com/patio-3.png",
      restaurantId = secondRestaurant,
    )
    withTables { tables =>
      for {
        _ <- tables.add(table)
        affected <- tables.update(updated)
        fetched <- tables.get(table.uuid)
      } yield {
        assertEquals(affected, 1)
        assertEquals(fetched, Some(updated))
      }
    }
  }

  test("update affects no rows for an unknown table") {
    withTables { tables =>
      tables.update(sampleTable(UUID.randomUUID())).map(affected => assertEquals(affected, 0))
    }
  }

  test("delete removes a persisted table") {
    val table = sampleTable(UUID.randomUUID())
    withTables { tables =>
      for {
        _ <- tables.add(table)
        deleted <- tables.delete(table.uuid)
        fetched <- tables.get(table.uuid)
      } yield {
        assertEquals(deleted, 1)
        assertEquals(fetched, None)
      }
    }
  }

  test("delete affects no rows for an unknown table") {
    withTables { tables =>
      tables.delete(UUID.randomUUID()).map(deleted => assertEquals(deleted, 0))
    }
  }
}
