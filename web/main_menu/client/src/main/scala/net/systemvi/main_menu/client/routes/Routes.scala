package net.systemvi.main_menu.client.routes

import com.raquo.waypoint.*
import Pages.*
import PageDecoders.given

import java.util.UUID

object Routes {
  val loginRoute = Route.static(LoginPage, root / "login" / endOfSegments)

  val welcomeRoute = Route.static(WelcomePage, root / "welcome" / endOfSegments)

  val restaurantsRoute = Route.static(RestaurantsPage, root / endOfSegments)

  val restaurantMenuRoute = Route[RestaurantMenuPage, UUID](
    encode = page => page.restaurantId,
    decode = restaurantId => RestaurantMenuPage(restaurantId),
    pattern = root / "restaurant" / segment[UUID] / endOfSegments,
  )

  val categoryMenuRoute = Route[CategoryMenuPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.categoryId),
    decode = args => CategoryMenuPage(args._1, args._2),
    pattern = root / "restaurant" / segment[UUID] / "category" / segment[UUID] / endOfSegments,
  )

  val subcategoryMenuRoute = Route[SubcategoryMenuPage, (UUID, UUID, UUID)](
    encode = page => (page.restaurantId, page.categoryId, page.subcategoryId),
    decode = args => SubcategoryMenuPage(args._1, args._2, args._3),
    pattern = root / "restaurant" / segment[UUID] / "category" / segment[UUID] / "subcategory" / segment[UUID] / endOfSegments,
  )

  val itemRoute = Route[ItemPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.itemId),
    decode = args => ItemPage(args._1, args._2),
    pattern = root / "restaurant" / segment[UUID] / "item" / segment[UUID] / endOfSegments,
  )

  val tableMenuRoute = Route[TableMenuPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.tableId),
    decode = args => TableMenuPage(args._1, args._2),
    pattern = root / "restaurant" / segment[UUID] / "table" / segment[UUID] / endOfSegments,
  )

  val tableItemRoute = Route[TableItemPage, (UUID, UUID, UUID)](
    encode = page => (page.restaurantId, page.tableId, page.itemId),
    decode = args => TableItemPage(args._1, args._2, args._3),
    pattern = root / "restaurant" / segment[UUID] / "table" / segment[UUID] / "item" / segment[UUID] / endOfSegments,
  )

  val adminRootRoute = Route[AdminDashboardPage, UUID](
    encode = page => page.restaurantId,
    decode = restaurantId => AdminDashboardPage(restaurantId),
    pattern = root / "admin" / "restaurant" / segment[UUID] / endOfSegments,
  )

  val adminDashboardRoute = Route[AdminDashboardPage, UUID](
    encode = page => page.restaurantId,
    decode = restaurantId => AdminDashboardPage(restaurantId),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "dashboard" / endOfSegments,
  )

  val adminTablesRoute = Route[AdminTablesPage, UUID](
    encode = page => page.restaurantId,
    decode = restaurantId => AdminTablesPage(restaurantId),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "tables" / endOfSegments,
  )

  val adminCategoriesRoute = Route[AdminCategoriesPage, UUID](
    encode = page => page.restaurantId,
    decode = restaurantId => AdminCategoriesPage(restaurantId),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "categories" / endOfSegments,
  )

  val adminCategoryRoute = Route[AdminCategoryPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.categoryId),
    decode = args => AdminCategoryPage(args._1, args._2),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "categories" / segment[UUID] / endOfSegments,
  )

  // Create routes carry the parent so the new record knows where it belongs;
  // a top-level category is parented by the category page it was created from.
  val adminCreateCategoryRoute = Route[AdminCreateCategoryPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.parentId),
    decode = args => AdminCreateCategoryPage(args._1, args._2),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "category" / "new" / "under" / segment[UUID] / endOfSegments,
  )

  val adminEditCategoryRoute = Route[AdminEditCategoryPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.categoryId),
    decode = args => AdminEditCategoryPage(args._1, args._2),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "category" / segment[UUID] / "edit" / endOfSegments,
  )

  val adminCreateItemRoute = Route[AdminCreateItemPage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.categoryId),
    decode = args => AdminCreateItemPage(args._1, args._2),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "category" / segment[UUID] / "item" / "new" / endOfSegments,
  )

  val adminEditItemRoute = Route[AdminEditItemPage, (UUID, UUID, UUID)](
    encode = page => (page.restaurantId, page.categoryId, page.itemId),
    decode = args => AdminEditItemPage(args._1, args._2, args._3),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "category" / segment[UUID] / "item" / segment[UUID] / "edit" / endOfSegments,
  )

  val adminCreateTableRoute = Route[AdminCreateTablePage, UUID](
    encode = page => page.restaurantId,
    decode = restaurantId => AdminCreateTablePage(restaurantId),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "table" / "new" / endOfSegments,
  )

  val adminEditTableRoute = Route[AdminEditTablePage, (UUID, UUID)](
    encode = page => (page.restaurantId, page.tableId),
    decode = args => AdminEditTablePage(args._1, args._2),
    pattern = root / "admin" / "restaurant" / segment[UUID] / "table" / segment[UUID] / "edit" / endOfSegments,
  )

  val notFoundRoute = Route.static(NotFoundPage, root / "404" / endOfSegments)
}
