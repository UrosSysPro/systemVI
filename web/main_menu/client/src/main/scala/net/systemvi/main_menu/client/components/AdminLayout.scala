package net.systemvi.main_menu.client.components

import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router

import java.util.UUID

/**
 * Reloadable data for admin pages: fetches immediately, and again whenever
 * `reloads` emits. Subscriptions are owned by whatever renders the signal.
 */
def reloadable[A](reloads: EventBus[Unit], fetch: => EventStream[A], initial: A): Signal[A] =
  EventStream
    .merge(EventStream.fromValue((), emitOnce = true), reloads.events)
    .flatMapSwitch(_ => fetch)
    .toSignal(initial)

/** Admin page shell: a collapsible side nav on the left, content on the right. */
def adminLayout(restaurantId: UUID, activeNav: Page, content: HtmlElement): HtmlElement = {
  val collapsed = Var(false)

  div(
    display.flex,
    flexDirection.row,
    alignItems.start,
    width.percent(100),
    minHeight.vh(100),
    backgroundColor("#f6f6f6"),
    sideNav(restaurantId, activeNav, collapsed),
    div(
      flex("1"),
      display.flex,
      flexDirection.column,
      alignItems.center,
      gap.rem(1),
      paddingTop.rem(1),
      paddingBottom.rem(2),
      paddingLeft.rem(1),
      paddingRight.rem(2),
      div(
        display <-- collapsed.signal.map(value => if value then "flex" else "none"),
        flexDirection.row,
        width.percent(100),
        button(
          "☰",
          onClick --> { _ => collapsed.set(false) },
          padding.rem(0.5),
          fontSize.rem(1.2),
          cursor.pointer,
          border("1px solid #cccccc"),
          borderRadius.rem(0.25),
          backgroundColor("#ffffff"),
        ),
      ),
      content,
    ),
  )
}

private def sideNav(restaurantId: UUID, activeNav: Page, collapsed: Var[Boolean]): HtmlElement =
  div(
    display <-- collapsed.signal.map(value => if value then "none" else "flex"),
    flexDirection.column,
    gap.rem(0.25),
    padding.rem(0.75),
    backgroundColor("#22272d"),
    color("#ffffff"),
    minHeight.vh(100),
    width.px(220),
    div(
      display.flex,
      flexDirection.row,
      alignItems.center,
      justifyContent.spaceBetween,
      paddingTop.rem(0.5),
      paddingBottom.rem(1),
      span(fontWeight.bold, fontSize.rem(1.2), "Admin"),
      button(
        "«",
        onClick --> { _ => collapsed.set(true) },
        color("#ffffff"),
        backgroundColor("transparent"),
        border("none"),
        cursor.pointer,
        fontSize.rem(1.2),
      ),
    ),
    navLink(activeNav, AdminDashboardPage(restaurantId), "Dashboard"),
    navLink(activeNav, AdminTablesPage(restaurantId), "Tables"),
    navLink(activeNav, AdminCategoriesPage(restaurantId), "Categories"),
    div(
      display.flex,
      flexDirection.column,
      paddingTop.rem(1),
      navLink(activeNav, RestaurantMenuPage(restaurantId), "View menu"),
    ),
  )

private def navLink(activeNav: Page, page: Page, label: String): HtmlElement =
  a(
    Router.navigateTo(page),
    display.block,
    paddingTop.rem(0.5),
    paddingBottom.rem(0.5),
    paddingLeft.rem(0.75),
    paddingRight.rem(0.75),
    borderRadius.rem(0.25),
    textDecoration("none"),
    cursor.pointer,
    color("#ffffff"),
    backgroundColor(if activeNav == page then "#3d4451" else "transparent"),
    label,
  )
