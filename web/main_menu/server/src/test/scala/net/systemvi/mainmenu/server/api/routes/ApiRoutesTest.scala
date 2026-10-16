package net.systemvi.mainmenu.server.api.routes

import cats.effect.*
import doobie.*
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import munit.CatsEffectSuite
import net.systemvi.mainmenu.common.dto.{CategoryDto, ItemDto, RestaurantTableDto}
import net.systemvi.mainmenu.server.{TestAppContext, TestDatabase}
import net.systemvi.mainmenu.server.persistance.contexts.*
import net.systemvi.mainmenu.server.persistance.models.{Category, Item, Restaurant, RestaurantTable}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*

import java.util.UUID

class ApiRoutesTest extends CatsEffectSuite {

  given Decoder[ItemDto] = deriveDecoder

  private def withApi[A](f: (Transactor[IO], HttpRoutes[IO]) => IO[A]): IO[A] =
    TestDatabase.transactor.use { xa =>
      given AppContext[IO] = TestAppContext.create(xa)
      f(xa, api)
    }

  test("GET /restaurants/{id}/categories returns that restaurant's category tree only") {
    withApi { (xa, routes) =>
      val restaurant = Restaurant(UUID.randomUUID(), "Bella Napoli", Some("desc"), "45 Via Roma", "img")
      val other = Restaurant(UUID.randomUUID(), "Other Place", None, "1 Other St", "img")
      val mains = Category(UUID.randomUUID(), "Mains", None, "img", None, Some(restaurant.uuid))
      val beef = Category(UUID.randomUUID(), "Beef", None, "img", Some(mains.uuid), Some(restaurant.uuid))
      val foreign = Category(UUID.randomUUID(), "Foreign", None, "img", None, Some(other.uuid))

      val restaurants = RestaurantContext.create[IO](xa)
      val categories = CategoryContext.create[IO](xa)
      for {
        _ <- restaurants.add(restaurant)
        _ <- restaurants.add(other)
        _ <- categories.add(mains)
        _ <- categories.add(beef)
        _ <- categories.add(foreign)
        response <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/restaurants/${restaurant.uuid}/categories"))
        )
        body <- response.as[List[CategoryDto]]
      } yield {
        assertEquals(response.status, Status.Ok)
        assertEquals(body.map(_.name), List("Mains"))
        assertEquals(body.head.children.map(_.name), List("Beef"))
      }
    }
  }

  test("GET /restaurants/{id}/tables returns only that restaurant's tables") {
    withApi { (xa, routes) =>
      val restaurantId = UUID.randomUUID()
      val otherRestaurantId = UUID.randomUUID()
      val table = RestaurantTable(UUID.randomUUID(), "Table 1", "img", restaurantId)
      val otherTable = RestaurantTable(UUID.randomUUID(), "Other Table", "img", otherRestaurantId)

      val tables = RestaurantTableContext.create[IO](xa)
      for {
        _ <- tables.add(table)
        _ <- tables.add(otherTable)
        response <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/restaurants/$restaurantId/tables"))
        )
        body <- response.as[List[RestaurantTableDto]]
      } yield {
        assertEquals(response.status, Status.Ok)
        assertEquals(body.map(_.name), List("Table 1"))
      }
    }
  }

  test("GET /items/category/{id} returns only that category's items") {
    withApi { (xa, routes) =>
      val categoryId = UUID.randomUUID()
      val otherCategoryId = UUID.randomUUID()
      val margherita = Item(UUID.randomUUID(), "Margherita", "img", Some("Tomato"), BigDecimal("8.50"), Some(categoryId))
      val marinara = Item(UUID.randomUUID(), "Marinara", "img", None, BigDecimal("7.00"), Some(otherCategoryId))

      val items = ItemContext.create[IO](xa)
      for {
        _ <- items.add(margherita)
        _ <- items.add(marinara)
        response <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/items/category/$categoryId"))
        )
        body <- response.as[List[ItemDto]]
      } yield {
        assertEquals(response.status, Status.Ok)
        assertEquals(body.map(_.name), List("Margherita"))
      }
    }
  }

  test("GET /category/{id} returns the category with its subcategories") {
    withApi { (xa, routes) =>
      val restaurantId = UUID.randomUUID()
      val mains = Category(UUID.randomUUID(), "Mains", None, "img", None, Some(restaurantId))
      val beef = Category(UUID.randomUUID(), "Beef", None, "img", Some(mains.uuid), Some(restaurantId))

      val categories = CategoryContext.create[IO](xa)
      for {
        _ <- categories.add(mains)
        _ <- categories.add(beef)
        response <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/category/${mains.uuid}"))
        )
        body <- response.as[CategoryDto]
      } yield {
        assertEquals(response.status, Status.Ok)
        assertEquals(body.name, "Mains")
        assertEquals(body.children.map(_.name), List("Beef"))
      }
    }
  }

  test("PUT /items/{id}/image stores the bytes and GET /image/item/{id} serves them back") {
    withApi { (xa, routes) =>
      val categoryId = UUID.randomUUID()
      val item = Item(UUID.randomUUID(), "Margherita", "/api/image/item/x", None, BigDecimal(9), Some(categoryId))

      val items = ItemContext.create[IO](xa)
      val payload = Array[Byte](1, 2, 3, 4, 5)
      for {
        _ <- items.add(item)
        upload <- routes.orNotFound.run(
          Request[IO](Method.PUT, Uri.unsafeFromString(s"/items/${item.uuid}/image")).withEntity(payload)
        )
        fetched <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/image/item/${item.uuid}"))
        )
        served <- fetched.body.compile.to(Array)
      } yield {
        assertEquals(upload.status, Status.Ok)
        assertEquals(fetched.status, Status.Ok)
        assertEquals(served.toList, payload.toList)
        assertEquals(fetched.contentType.map(_.mediaType), Some(MediaType.image.png))
      }
    }
  }

  test("PUT /category/{id}/image stores the bytes and GET /image/category/{id} serves them back") {
    withApi { (xa, routes) =>
      val category = Category(UUID.randomUUID(), "Mains", None, "/api/image/category/x", None, Some(UUID.randomUUID()))

      val categories = CategoryContext.create[IO](xa)
      val payload = Array[Byte](9, 8, 7)
      for {
        _ <- categories.add(category)
        upload <- routes.orNotFound.run(
          Request[IO](Method.PUT, Uri.unsafeFromString(s"/category/${category.uuid}/image")).withEntity(payload)
        )
        fetched <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/image/category/${category.uuid}"))
        )
        served <- fetched.body.compile.to(Array)
      } yield {
        assertEquals(upload.status, Status.Ok)
        assertEquals(fetched.status, Status.Ok)
        assertEquals(served.toList, payload.toList)
      }
    }
  }

  test("GET /image/{kind}/{id} is 404 when no image was uploaded") {
    withApi { (xa, routes) =>
      val category = Category(UUID.randomUUID(), "Mains", None, "img", None, Some(UUID.randomUUID()))

      val categories = CategoryContext.create[IO](xa)
      for {
        _ <- categories.add(category)
        missing <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/image/category/${category.uuid}"))
        )
      } yield assertEquals(missing.status, Status.NotFound)
    }
  }

  test("PUT /items/{id}/image rejects an empty body") {
    withApi { (xa, routes) =>
      val item = Item(UUID.randomUUID(), "Margherita", "img", None, BigDecimal(9), Some(UUID.randomUUID()))

      val items = ItemContext.create[IO](xa)
      for {
        _ <- items.add(item)
        response <- routes.orNotFound.run(
          Request[IO](Method.PUT, Uri.unsafeFromString(s"/items/${item.uuid}/image"))
        )
      } yield assertEquals(response.status, Status.BadRequest)
    }
  }

  test("image bytes never leak into a listing response") {
    withApi { (xa, routes) =>
      val categoryId = UUID.randomUUID()
      val item = Item(UUID.randomUUID(), "Margherita", "img", None, BigDecimal(9), Some(categoryId))

      val items = ItemContext.create[IO](xa)
      for {
        _ <- items.add(item)
        _ <- routes.orNotFound.run(
          Request[IO](Method.PUT, Uri.unsafeFromString(s"/items/${item.uuid}/image")).withEntity(Array[Byte](1, 2, 3))
        )
        listing <- routes.orNotFound.run(
          Request[IO](Method.GET, Uri.unsafeFromString(s"/items/category/$categoryId"))
        )
        raw <- listing.body.compile.to(Array).map(new String(_, "UTF-8"))
      } yield {
        assertEquals(listing.status, Status.Ok)
        // The blob is stored and served by the proxy, but the listing JSON only
        // carries fields, so the bytes themselves must not appear here.
        assert(raw.contains("\"Margherita\""), raw)
        assert(!raw.contains("imageBlob"), s"listing should not mention the blob column: $raw")
        assert(!raw.contains("AQID"), s"image bytes should not appear in the listing: $raw")
      }
    }
  }
}
