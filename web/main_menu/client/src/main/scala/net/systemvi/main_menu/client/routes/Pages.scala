package net.systemvi.main_menu.client.routes

object Pages {
  sealed abstract class Page(val title: String)

  case object CategoriesPage extends Page("Categories")

  case object ItemsPage extends Page("Items")

  case object NotFoundPage extends Page("404")
}
