package net.systemvi.main_menu.client.routes

import java.util.UUID

object Pages {
  sealed abstract class Page(val title: String)

  case object LoginPage extends Page("Sign in")

  case object WelcomePage extends Page("Welcome")

  case object RestaurantsPage extends Page("Restaurants")

  case class RestaurantMenuPage(restaurantId: UUID) extends Page("Menu")

  case class CategoryMenuPage(restaurantId: UUID, categoryId: UUID) extends Page("Category")

  case class SubcategoryMenuPage(restaurantId: UUID, categoryId: UUID, subcategoryId: UUID) extends Page("Category")

  case class ItemPage(restaurantId: UUID, itemId: UUID) extends Page("Item")

  case class TableMenuPage(restaurantId: UUID, tableId: UUID) extends Page("Menu")

  case class TableItemPage(restaurantId: UUID, tableId: UUID, itemId: UUID) extends Page("Item")

  case class AdminDashboardPage(restaurantId: UUID) extends Page("Dashboard")

  case class AdminTablesPage(restaurantId: UUID) extends Page("Tables")

  case class AdminCategoriesPage(restaurantId: UUID) extends Page("Categories")

  case class AdminCategoryPage(restaurantId: UUID, categoryId: UUID) extends Page("Category")

  case class AdminCreateCategoryPage(restaurantId: UUID, parentId: UUID) extends Page("New category")

  case class AdminEditCategoryPage(restaurantId: UUID, categoryId: UUID) extends Page("Edit category")

  case class AdminCreateItemPage(restaurantId: UUID, categoryId: UUID) extends Page("New item")

  case class AdminEditItemPage(restaurantId: UUID, categoryId: UUID, itemId: UUID) extends Page("Edit item")

  case class AdminCreateTablePage(restaurantId: UUID) extends Page("New table")

  case class AdminEditTablePage(restaurantId: UUID, tableId: UUID) extends Page("Edit table")

  case object NotFoundPage extends Page("404")
}
