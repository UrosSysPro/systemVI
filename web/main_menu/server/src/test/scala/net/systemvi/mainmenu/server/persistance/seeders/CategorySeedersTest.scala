package net.systemvi.mainmenu.server.persistance.seeders

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.contexts.CategoryContext
import net.systemvi.mainmenu.server.services.CategoryTree

class CategorySeedersTest extends CatsEffectSuite {

  test("seeding populates the default category tree") {
    TestDatabase.transactor.use { xa =>
      val categories = CategoryContext.create[IO](xa)
      for {
        _ <- CategorySeeders.seed(xa)
        all <- categories.getAll()
      } yield {
        val forest = CategoryTree.forest(all)

        assertEquals(
          forest.map(_.name),
          List("Desserts", "Drinks", "Main Course", "Side Dishes"),
        )

        val drinks = forest.find(_.name == "Drinks").get
        assertEquals(drinks.children.map(_.name), List("Alcoholic Drinks", "Non-Alcoholic Drinks"))
        assert(
          drinks.children.forall(_.parentId.contains(drinks.uuid)),
          "nested categories should point at their parent id",
        )
        assert(drinks.children.forall(_.children.isEmpty), "seeded leaves should have no children")
      }
    }
  }
}
