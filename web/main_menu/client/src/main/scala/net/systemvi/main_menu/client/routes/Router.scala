package net.systemvi.main_menu.client.routes

import com.raquo.waypoint.*
import Pages.*
import Routes.*

object Router extends Router[Page](
  routes = List(categoriesRoute, itemsRoute, notFoundRoute),
  getPageTitle = page => page.title,
  serializePage = (page: Page) => page match {
    case CategoriesPage => "categories"
    case ItemsPage => "items"
    case NotFoundPage => "not-found"
  },
  deserializePage = (value: String) => value match {
    case "categories" => CategoriesPage
    case "items" => ItemsPage
    case _ => NotFoundPage
  },
)
