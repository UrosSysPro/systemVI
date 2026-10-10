package net.systemvi.main_menu.client.routes

import java.util.UUID

object Pages {
  sealed abstract class Page(val title: String)

  case object RestaurantsPage extends Page("Restaurants")

  case class RestaurantMenuPage(restaurantId: UUID) extends Page("Menu")

  case class CategoryMenuPage(restaurantId: UUID, categoryId: UUID) extends Page("Category")

  case class SubcategoryMenuPage(restaurantId: UUID, categoryId: UUID, subcategoryId: UUID) extends Page("Category")

  case class AdminDashboardPage(restaurantId: UUID) extends Page("Dashboard")

  case class AdminTablesPage(restaurantId: UUID) extends Page("Tables")

  case class AdminCategoriesPage(restaurantId: UUID) extends Page("Categories")

  case class AdminCategoryPage(restaurantId: UUID, categoryId: UUID) extends Page("Category")

  case object NotFoundPage extends Page("404")
}
