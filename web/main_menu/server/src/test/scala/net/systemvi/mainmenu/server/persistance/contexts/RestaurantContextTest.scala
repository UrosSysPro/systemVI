package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.Restaurant

import java.util.UUID

class RestaurantContextTest extends CatsEffectSuite {

  private def withRestaurants[A](f: RestaurantContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(RestaurantContext.create[IO]).use(f)

  private def sampleRestaurant(name: String = "Trattoria"): Restaurant =
    Restaurant(
      uuid = UUID.randomUUID(),
      name = name,
      description = Some(s"$name description"),
      address = "123 Main Street",
      imageUrl = s"https://cdn.example.com/${name.toLowerCase.replace(' ', '-')}.png",
    )

  test("add persists a restaurant that can then be fetched by id") {
    val restaurant = sampleRestaurant()
    withRestaurants { restaurants =>
      for {
        inserted <- restaurants.add(restaurant)
        fetched <- restaurants.get(restaurant.uuid)
      } yield {
        assertEquals(inserted, 1)
        assertEquals(fetched, Some(restaurant))
      }
    }
  }

  test("get returns None for an unknown id") {
    withRestaurants { restaurants =>
      restaurants.get(UUID.randomUUID()).map(fetched => assertEquals(fetched, None))
    }
  }

  test("getAll returns every persisted restaurant") {
    val a = sampleRestaurant("Trattoria")
    val b = sampleRestaurant("Bistro")
    withRestaurants { restaurants =>
      for {
        _ <- restaurants.add(a)
        _ <- restaurants.add(b)
        all <- restaurants.getAll()
      } yield assertEquals(all.toSet, Set(a, b))
    }
  }

  test("update replaces every field of an existing restaurant") {
    val restaurant = sampleRestaurant()
    val updated = restaurant.copy(
      name = "Osteria",
      description = None,
      address = "456 Side Road",
      imageUrl = "https://cdn.example.com/osteria.png",
    )
    withRestaurants { restaurants =>
      for {
        _ <- restaurants.add(restaurant)
        affected <- restaurants.update(updated)
        fetched <- restaurants.get(restaurant.uuid)
      } yield {
        assertEquals(affected, 1)
        assertEquals(fetched, Some(updated))
      }
    }
  }

  test("update affects no rows for an unknown restaurant") {
    withRestaurants { restaurants =>
      restaurants.update(sampleRestaurant()).map(affected => assertEquals(affected, 0))
    }
  }

  test("delete removes a persisted restaurant") {
    val restaurant = sampleRestaurant()
    withRestaurants { restaurants =>
      for {
        _ <- restaurants.add(restaurant)
        deleted <- restaurants.delete(restaurant.uuid)
        fetched <- restaurants.get(restaurant.uuid)
      } yield {
        assertEquals(deleted, 1)
        assertEquals(fetched, None)
      }
    }
  }

  test("delete affects no rows for an unknown restaurant") {
    withRestaurants { restaurants =>
      restaurants.delete(UUID.randomUUID()).map(deleted => assertEquals(deleted, 0))
    }
  }
}
