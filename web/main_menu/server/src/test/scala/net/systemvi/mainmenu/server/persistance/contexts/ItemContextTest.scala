package net.systemvi.mainmenu.server.persistance.contexts

import cats.effect.*
import munit.CatsEffectSuite
import net.systemvi.mainmenu.server.TestDatabase
import net.systemvi.mainmenu.server.persistance.models.Item

import java.util.UUID

class ItemContextTest extends CatsEffectSuite {

  private def withItems[A](f: ItemContext[IO] => IO[A]): IO[A] =
    TestDatabase.transactor.map(ItemContext.create[IO]).use(f)

  private def sampleItem(name: String = "Margherita"): Item =
    Item(
      uuid = UUID.randomUUID(),
      name = name,
      imageUrl = "https://cdn.example.com/margherita.png",
      description = Some("Tomato, mozzarella and basil"),
      price = BigDecimal("8.50"),
      categoryId = None,
    )

  test("add persists an item that can then be fetched by id") {
    val item = sampleItem()
    withItems { items =>
      for {
        inserted <- items.add(item)
        fetched <- items.get(item.uuid)
      } yield {
        assertEquals(inserted, 1)
        assertEquals(fetched, Some(item))
      }
    }
  }

  test("get returns None for an unknown id") {
    withItems { items =>
      items.get(UUID.randomUUID()).map(fetched => assertEquals(fetched, None))
    }
  }

  test("add persists an item that links to a category") {
    val categoryId = UUID.randomUUID()
    val item = sampleItem().copy(categoryId = Some(categoryId))
    withItems { items =>
      for {
        _ <- items.add(item)
        fetched <- items.get(item.uuid)
      } yield {
        assertEquals(fetched, Some(item))
        assertEquals(fetched.flatMap(_.categoryId), Some(categoryId))
      }
    }
  }

  test("getAll returns every persisted item") {
    val a = sampleItem("Margherita")
    val b = sampleItem("Pepperoni").copy(price = BigDecimal("10"))
    withItems { items =>
      for {
        _ <- items.add(a)
        _ <- items.add(b)
        all <- items.getAll()
      } yield assertEquals(all.toSet, Set(a, b))
    }
  }

  test("update replaces every field of an existing item") {
    val item = sampleItem()
    val updated = item.copy(
      name = "Marinara",
      imageUrl = "https://cdn.example.com/marinara.png",
      description = None,
      price = BigDecimal("7.25"),
    )
    withItems { items =>
      for {
        _ <- items.add(item)
        affected <- items.update(updated)
        fetched <- items.get(item.uuid)
      } yield {
        assertEquals(affected, 1)
        assertEquals(fetched, Some(updated))
      }
    }
  }

  test("update affects no rows for an unknown item") {
    withItems { items =>
      items.update(sampleItem()).map(affected => assertEquals(affected, 0))
    }
  }

  test("delete removes a persisted item") {
    val item = sampleItem()
    withItems { items =>
      for {
        _ <- items.add(item)
        deleted <- items.delete(item.uuid)
        fetched <- items.get(item.uuid)
      } yield {
        assertEquals(deleted, 1)
        assertEquals(fetched, None)
      }
    }
  }

  test("delete affects no rows for an unknown item") {
    withItems { items =>
      items.delete(UUID.randomUUID()).map(deleted => assertEquals(deleted, 0))
    }
  }
}
