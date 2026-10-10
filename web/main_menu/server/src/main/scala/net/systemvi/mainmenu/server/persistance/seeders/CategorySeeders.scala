package net.systemvi.mainmenu.server.persistance.seeders

import cats.*
import cats.implicits.*
import cats.effect.*
import doobie.util.transactor.Transactor
import net.systemvi.mainmenu.server.persistance.models.Category
import net.systemvi.mainmenu.server.persistance.contexts.CategoryContext

import java.util.UUID

object CategorySeeders {
  def seed[F[_]: MonadCancelThrow](xa: Transactor[F]): F[Unit] = {
    val categories = CategoryContext.create(xa)

    val drinks = Category(
      UUID.randomUUID(),
      "Drinks",
      Some("Everything you can drink"),
      "https://cdn.example.com/categories/drinks.png",
      None,
    )
    val alcoholicDrinks = Category(
      UUID.randomUUID(),
      "Alcoholic Drinks",
      Some("Beer, wine and cocktails"),
      "https://cdn.example.com/categories/alcoholic-drinks.png",
      Some(drinks.uuid),
    )
    val nonAlcoholicDrinks = Category(
      UUID.randomUUID(),
      "Non-Alcoholic Drinks",
      Some("Soft drinks, juices, coffee and tea"),
      "https://cdn.example.com/categories/non-alcoholic-drinks.png",
      Some(drinks.uuid),
    )

    val samples = List(
      drinks,
      alcoholicDrinks,
      nonAlcoholicDrinks,
      Category(
        UUID.randomUUID(),
        "Main Course",
        Some("Signature main dishes"),
        "https://cdn.example.com/categories/main-course.png",
        None,
      ),
      Category(
        UUID.randomUUID(),
        "Side Dishes",
        Some("Appetizers and side orders"),
        "https://cdn.example.com/categories/side-dishes.png",
        None,
      ),
      Category(
        UUID.randomUUID(),
        "Desserts",
        Some("Sweet treats to finish the meal"),
        "https://cdn.example.com/categories/desserts.png",
        None,
      ),
    )

    samples.traverse_(categories.add)
  }
}
