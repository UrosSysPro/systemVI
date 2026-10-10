package net.systemvi.mainmenu.server.persistance.seeders

import cats.*
import cats.implicits.*
import cats.effect.*
import doobie.util.transactor.Transactor
import net.systemvi.mainmenu.server.persistance.contexts.*
import net.systemvi.mainmenu.server.persistance.models.*

import java.util.UUID

/**
 * Seeds a self-contained restaurant graph.
 *
 * Each restaurant owns its tables, categories and items; nothing is shared
 * between restaurants, so an identical dish served in two restaurants is two
 * separate item rows.
 */
object RestaurantSeeders {

  private val imageBase = "https://cdn.example.com/main-menu"

  /** Five top-level categories per restaurant; two of them have subcategories. */
  private val menuPlan: List[(String, List[String])] = List(
    "Starters" -> Nil,
    "Mains"    -> List("Beef", "Seafood"),
    "Pizza"    -> List("Classic", "Specialty"),
    "Desserts" -> Nil,
    "Drinks"   -> Nil,
  )

  private val restaurantPlans: List[(String, String, String)] = List(
    ("The Copper Skillet", "123 Copper Street", "Hearty seasonal comfort food"),
    ("Bella Napoli", "45 Via Roma", "Neapolitan pizza and pasta"),
  )

  def seed[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Unit] = {
    val restaurants = RestaurantContext.create(xa)
    val tables = RestaurantTableContext.create(xa)
    val categories = CategoryContext.create(xa)
    val items = ItemContext.create(xa)

    val seeded = restaurantPlans.map { case (name, address, description) =>
      buildRestaurant(
        Restaurant(
          UUID.randomUUID(),
          name,
          Some(description),
          address,
          s"$imageBase/restaurants/${slug(name)}.png",
        )
      )
    }

    for {
      _ <- seeded.map(_._1).traverse(restaurants.add)
      _ <- seeded.flatMap(_._2).traverse(tables.add)
      _ <- seeded.flatMap(_._3).traverse(categories.add)
      _ <- seeded.flatMap(_._4).traverse(items.add)
    } yield ()
  }

  /** Builds (restaurant, tables, categories, items) for a single restaurant. */
  private def buildRestaurant(
    restaurant: Restaurant
  ): (Restaurant, List[RestaurantTable], List[Category], List[Item]) = {
    val tables = (1 to 3).toList.map { number =>
      RestaurantTable(
        UUID.randomUUID(),
        s"Table $number",
        s"$imageBase/tables/${slug(restaurant.name)}-$number.png",
        restaurant.uuid,
      )
    }

    val parentCategories = menuPlan.map { case (name, _) =>
      Category(UUID.randomUUID(), name, Some(s"$name at ${restaurant.name}"), imageUrl("categories", name), None, Some(restaurant.uuid))
    }

    val childCategories = menuPlan.zip(parentCategories).flatMap { case ((_, children), parent) =>
      children.map { name =>
        Category(UUID.randomUUID(), name, Some(s"$name at ${restaurant.name}"), imageUrl("categories", name), Some(parent.uuid), Some(restaurant.uuid))
      }
    }

    val categories = parentCategories ++ childCategories

    val items = categories.zipWithIndex.flatMap { case (category, index) =>
      val count = 5 + (index % 6) // between 5 and 10 items per category
      (1 to count).toList.map { number =>
        Item(
          UUID.randomUUID(),
          s"${category.name} $number",
          imageUrl("items", s"${category.name}-$number"),
          Some(s"${category.name} number $number"),
          BigDecimal(500 + index * 50 + number * 25) / 100,
          Some(category.uuid),
        )
      }
    }

    (restaurant, tables, categories, items)
  }

  private def imageUrl(kind: String, name: String): String =
    s"$imageBase/$kind/${slug(name)}.png"

  private def slug(value: String): String =
    value.toLowerCase.replace(' ', '-')
}
