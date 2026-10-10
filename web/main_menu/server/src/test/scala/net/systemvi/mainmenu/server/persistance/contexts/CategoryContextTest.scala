package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.Category

import java.util.UUID

class CategoryContextTest extends CatsEffectSuite {

  private def withCategories[A](f: CategoryContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(CategoryContext.create[IO]).use(f)

  private def sampleCategory(
    name: String = "Drinks",
    parentId: Option[UUID] = None,
  ): Category =
    Category(
      uuid = UUID.randomUUID(),
      name = name,
      description = Some(s"$name description"),
      imageUrl = s"https://cdn.example.com/categories/${name.toLowerCase.replace(' ', '-')}.png",
      parentId = parentId,
    )

  test("add persists a root category and reads back a null parentId as None") {
    val root = sampleCategory()
    withCategories { categories =>
      for {
        inserted <- categories.add(root)
        fetched <- categories.get(root.uuid)
      } yield {
        assertEquals(inserted, 1)
        assertEquals(fetched, Some(root))
        assertEquals(fetched.flatMap(_.parentId), None)
      }
    }
  }

  test("add persists a nested category with its parentId") {
    val root = sampleCategory()
    val child = sampleCategory("Alcoholic Drinks", Some(root.uuid))
    withCategories { categories =>
      for {
        _ <- categories.add(root)
        _ <- categories.add(child)
        fetched <- categories.get(child.uuid)
      } yield {
        assertEquals(fetched, Some(child))
        assertEquals(fetched.flatMap(_.parentId), Some(root.uuid))
      }
    }
  }

  test("get returns None for an unknown id") {
    withCategories { categories =>
      categories.get(UUID.randomUUID()).map(fetched => assertEquals(fetched, None))
    }
  }

  test("getAll returns every persisted category") {
    val root = sampleCategory()
    val child = sampleCategory("Alcoholic Drinks", Some(root.uuid))
    withCategories { categories =>
      for {
        _ <- categories.add(root)
        _ <- categories.add(child)
        all <- categories.getAll()
      } yield assertEquals(all.toSet, Set(root, child))
    }
  }

  test("update replaces every field, including the parent") {
    val firstParent = sampleCategory("Drinks")
    val secondParent = sampleCategory("Food")
    val category = sampleCategory("Wine", Some(firstParent.uuid))
    val updated = category.copy(
      name = "Red Wine",
      description = None,
      imageUrl = "https://cdn.example.com/categories/red-wine.png",
      parentId = Some(secondParent.uuid),
    )
    withCategories { categories =>
      for {
        _ <- categories.add(firstParent)
        _ <- categories.add(secondParent)
        _ <- categories.add(category)
        affected <- categories.update(updated)
        fetched <- categories.get(category.uuid)
      } yield {
        assertEquals(affected, 1)
        assertEquals(fetched, Some(updated))
      }
    }
  }

  test("update affects no rows for an unknown category") {
    withCategories { categories =>
      categories.update(sampleCategory()).map(affected => assertEquals(affected, 0))
    }
  }

  test("delete removes a persisted category") {
    val category = sampleCategory()
    withCategories { categories =>
      for {
        _ <- categories.add(category)
        deleted <- categories.delete(category.uuid)
        fetched <- categories.get(category.uuid)
      } yield {
        assertEquals(deleted, 1)
        assertEquals(fetched, None)
      }
    }
  }

  test("delete affects no rows for an unknown category") {
    withCategories { categories =>
      categories.delete(UUID.randomUUID()).map(deleted => assertEquals(deleted, 0))
    }
  }
}
