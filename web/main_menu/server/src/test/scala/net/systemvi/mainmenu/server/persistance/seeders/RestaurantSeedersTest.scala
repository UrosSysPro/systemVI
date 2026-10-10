package net.systemvi.mainmenu.server.persistance.seeders

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.contexts.*

class RestaurantSeedersTest extends CatsEffectSuite {

  test("seeding creates two restaurants, each with tables, nested categories and 5-10 items per category") {
    TestDatabase.transactor.use { xa =>
      val restaurants = RestaurantContext.create[IO](xa)
      val tables = RestaurantTableContext.create[IO](xa)
      val categories = CategoryContext.create[IO](xa)
      val items = ItemContext.create[IO](xa)

      for {
        _ <- RestaurantSeeders.seed(xa)
        allRestaurants <- restaurants.getAll()
        allTables <- tables.getAll()
        allCategories <- categories.getAll()
        allItems <- items.getAll()
      } yield {
        assertEquals(allRestaurants.size, 2)
        assertEquals(allTables.size, 6)
        assertEquals(allCategories.size, 18)
        assertEquals(allItems.size, 126)

        // each restaurant has exactly three of its own tables
        val tablesByRestaurant = allTables.groupBy(_.restaurantId)
        assertEquals(tablesByRestaurant.keySet, allRestaurants.map(_.uuid).toSet)
        assert(tablesByRestaurant.values.forall(_.size == 3))

        // each restaurant has 5 top-level categories, 2 of which have children
        val categoriesByRestaurant = allCategories.groupBy(_.restaurantId)
        assert(categoriesByRestaurant.keySet == allRestaurants.map(restaurant => Some(restaurant.uuid)).toSet)
        categoriesByRestaurant.values.foreach { restaurantCategories =>
          val ids = restaurantCategories.map(_.uuid).toSet
          val parents = restaurantCategories.filter(_.parentId.isEmpty)
          val children = restaurantCategories.filter(_.parentId.isDefined)

          assertEquals(parents.size, 5)
          assertEquals(children.size, 4)
          assertEquals(parents.count(parent => children.exists(_.parentId.contains(parent.uuid))), 2)
          assert(children.forall(child => child.parentId.exists(ids.contains)), "children must belong to their parent's restaurant")
        }

        // parent and child categories both carry items, 5 to 10 each
        val itemsByCategory = allItems.groupBy(_.categoryId)
        allCategories.foreach { category =>
          val count = itemsByCategory.getOrElse(Some(category.uuid), Nil).size
          assert(count >= 5 && count <= 10, s"category '${category.name}' has $count items")
        }

        // items are never reused: each belongs to exactly one category of one restaurant
        val categoryToRestaurant = allCategories.map(category => category.uuid -> category.restaurantId).toMap
        assert(allItems.forall(item => item.categoryId.exists(categoryToRestaurant.contains)))
        assertEquals(allItems.map(_.uuid).distinct.size, allItems.size)
      }
    }
  }
}
