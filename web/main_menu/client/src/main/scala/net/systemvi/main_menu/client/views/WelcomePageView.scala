package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.UserDto
import net.systemvi.main_menu.client.api.{AuthApi, MenuApi}
import net.systemvi.main_menu.client.components.{linkRow, listColumn, pageColumn, pageShell, pageTitle}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router

import java.util.UUID

/**
 * Post sign-in landing page. Each role is sent to its own home: customers to the
 * public menu, staff to the restaurant picker for their management area.
 */
def WelcomePageView(): HtmlElement = {
  val session: Signal[Option[Option[UserDto]]] = AuthApi.session.toSignal(None)

  pageShell(List(
    pageColumn(List(
      div(child <-- session.map {
        case None =>
          div(color("#666666"), "Signing you in…")
        case Some(None) =>
          div(Router.navigateTo(LoginPage))
        case Some(Some(user)) if user.role == "CUSTOMER" =>
          div(Router.navigateTo(RestaurantsPage))
        case Some(Some(user)) =>
          staffLanding(user)
      }),
    ))
  ))
}

/** Staff (admin/waiter) landing: greet, then let them choose a restaurant to manage. */
private def staffLanding(user: UserDto): HtmlElement = {
  val restaurants = MenuApi.restaurants.toSignal(Nil)

  val destination: UUID => Page = user.role match {
    case "ADMIN" => restaurantId => AdminDashboardPage(restaurantId)
    case _ => restaurantId => AdminTablesPage(restaurantId)
  }

  div(
    display.flex,
    flexDirection.column,
    gap.rem(1),
    width.percent(100),
    pageTitle(s"Welcome, ${user.name.getOrElse("there")}"),
    span(color("#666666"), s"Signed in as ${user.role}"),
    div(
      display.flex,
      flexDirection.column,
      gap.rem(0.5),
      h2(fontSize.rem(1.25), fontWeight.bold, color("#22272d"), "Pick a restaurant"),
      div(child <-- restaurants.map {
        case Nil => div("No restaurants yet.")
        case list => listColumn(list.map(restaurant => linkRow(restaurant.name, destination(restaurant.uuid))))
      }),
    ),
  )
}
