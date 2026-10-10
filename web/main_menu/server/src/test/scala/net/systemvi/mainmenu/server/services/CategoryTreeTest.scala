package net.systemvi.mainmenu.server.services

import munit.FunSuite
import net.systemvi.mainmenu.common.dto.CategoryDto
import net.systemvi.mainmenu.server.persistance.models.Category

import java.util.UUID

class CategoryTreeTest extends FunSuite {

  private val drinksId = UUID.randomUUID()
  private val alcoholicId = UUID.randomUUID()
  private val wineId = UUID.randomUUID()

  private val drinks = Category(drinksId, "Drinks", Some("Everything you can drink"), "drinks.png", None)
  private val alcoholic =
    Category(alcoholicId, "Alcoholic Drinks", Some("Beer, wine and cocktails"), "alcoholic.png", Some(drinksId))
  private val wine = Category(wineId, "Wine", None, "wine.png", Some(alcoholicId))
  private val nonAlcoholic =
    Category(UUID.randomUUID(), "Non-Alcoholic Drinks", None, "soft.png", Some(drinksId))
  private val mainCourse = Category(UUID.randomUUID(), "Main Course", None, "main.png", None)

  private val categories = List(drinks, alcoholic, wine, nonAlcoholic, mainCourse)

  test("forest nests descendants under their parent, sorted by name") {
    val forest = CategoryTree.forest(categories)

    assertEquals(forest.map(_.name), List("Drinks", "Main Course"))

    val drinksDto = forest.head
    assertEquals(drinksDto.children.map(_.name), List("Alcoholic Drinks", "Non-Alcoholic Drinks"))

    val alcoholicDto = drinksDto.children.head
    assertEquals(alcoholicDto.children.map(_.name), List("Wine"))
    assertEquals(alcoholicDto.children.head.children, Nil)
  }

  test("every dto carries its parentId and children") {
    val drinksDto = CategoryTree.forest(categories).head
    val expected = CategoryDto(
      drinksId,
      "Drinks",
      Some("Everything you can drink"),
      "drinks.png",
      None,
      List(
        CategoryDto(alcoholicId, "Alcoholic Drinks", Some("Beer, wine and cocktails"), "alcoholic.png", Some(drinksId),
          List(CategoryDto(wineId, "Wine", None, "wine.png", Some(alcoholicId), Nil))),
        CategoryDto(nonAlcoholic.uuid, "Non-Alcoholic Drinks", None, "soft.png", Some(drinksId), Nil),
      ),
    )
    assertEquals(drinksDto, expected)
  }

  test("subtree returns the node together with only its descendants") {
    val subtree = CategoryTree.subtree(categories, drinksId)

    assertEquals(subtree.map(_.name), Some("Drinks"))
    assertEquals(subtree.map(_.children.map(_.name)), Some(List("Alcoholic Drinks", "Non-Alcoholic Drinks")))
    assertEquals(subtree.map(_.children.head.children.map(_.name)), Some(List("Wine")))
  }

  test("subtree of a leaf has no children") {
    assertEquals(CategoryTree.subtree(categories, wineId).map(_.children), Some(Nil))
  }

  test("subtree returns None for an unknown id") {
    assertEquals(CategoryTree.subtree(categories, UUID.randomUUID()), None)
  }
}
