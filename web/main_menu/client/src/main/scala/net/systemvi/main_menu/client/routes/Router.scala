package net.systemvi.main_menu.client.routes

import com.raquo.waypoint.*
import Pages.*
import Routes.*

import java.util.UUID
import scala.util.*

object Router extends Router[Page](
  routes = List(
    loginRoute,
    welcomeRoute,
    restaurantsRoute,
    restaurantMenuRoute,
    categoryMenuRoute,
    subcategoryMenuRoute,
    itemRoute,
    tableMenuRoute,
    tableItemRoute,
    adminDashboardRoute,
    adminTablesRoute,
    adminCategoriesRoute,
    adminCategoryRoute,
    notFoundRoute,
  ),
  getPageTitle = page => page.title,
  serializePage = (page: Page) => page match {
    case LoginPage => "login"
    case WelcomePage => "welcome"
    case RestaurantsPage => "restaurants"
    case RestaurantMenuPage(restaurantId) => s"restaurant|$restaurantId"
    case CategoryMenuPage(restaurantId, categoryId) => s"category|$restaurantId|$categoryId"
    case SubcategoryMenuPage(restaurantId, categoryId, subcategoryId) => s"subcategory|$restaurantId|$categoryId|$subcategoryId"
    case ItemPage(restaurantId, itemId) => s"item|$restaurantId|$itemId"
    case TableMenuPage(restaurantId, tableId) => s"table|$restaurantId|$tableId"
    case TableItemPage(restaurantId, tableId, itemId) => s"table-item|$restaurantId|$tableId|$itemId"
    case AdminDashboardPage(restaurantId) => s"admin-dashboard|$restaurantId"
    case AdminTablesPage(restaurantId) => s"admin-tables|$restaurantId"
    case AdminCategoriesPage(restaurantId) => s"admin-categories|$restaurantId"
    case AdminCategoryPage(restaurantId, categoryId) => s"admin-category|$restaurantId|$categoryId"
    case NotFoundPage => "not-found"
  },
  deserializePage = (value: String) =>
    Try(value.split('|').toList match {
      case "restaurant" :: restaurantId :: Nil =>
        RestaurantMenuPage(UUID.fromString(restaurantId))
      case "category" :: restaurantId :: categoryId :: Nil =>
        CategoryMenuPage(UUID.fromString(restaurantId), UUID.fromString(categoryId))
      case "subcategory" :: restaurantId :: categoryId :: subcategoryId :: Nil =>
        SubcategoryMenuPage(UUID.fromString(restaurantId), UUID.fromString(categoryId), UUID.fromString(subcategoryId))
      case "item" :: restaurantId :: itemId :: Nil =>
        ItemPage(UUID.fromString(restaurantId), UUID.fromString(itemId))
      case "table" :: restaurantId :: tableId :: Nil =>
        TableMenuPage(UUID.fromString(restaurantId), UUID.fromString(tableId))
      case "table-item" :: restaurantId :: tableId :: itemId :: Nil =>
        TableItemPage(UUID.fromString(restaurantId), UUID.fromString(tableId), UUID.fromString(itemId))
      case "admin-dashboard" :: restaurantId :: Nil =>
        AdminDashboardPage(UUID.fromString(restaurantId))
      case "admin-tables" :: restaurantId :: Nil =>
        AdminTablesPage(UUID.fromString(restaurantId))
      case "admin-categories" :: restaurantId :: Nil =>
        AdminCategoriesPage(UUID.fromString(restaurantId))
      case "admin-category" :: restaurantId :: categoryId :: Nil =>
        AdminCategoryPage(UUID.fromString(restaurantId), UUID.fromString(categoryId))
      case "login" :: Nil =>
        LoginPage
      case "welcome" :: Nil =>
        WelcomePage
      case "restaurants" :: Nil =>
        RestaurantsPage
      case _ =>
        NotFoundPage
    }).getOrElse(NotFoundPage),
)
