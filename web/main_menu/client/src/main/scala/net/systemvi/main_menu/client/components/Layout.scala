package net.systemvi.main_menu.client.components

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router

/** Full-page wrapper: a centered, light background with a responsive column. */
def pageShell(children: List[HtmlElement]): HtmlElement =
  div(
    display.flex,
    flexDirection.column,
    alignItems.center,
    paddingTop.rem(2),
    paddingBottom.rem(2),
    paddingLeft.rem(1),
    paddingRight.rem(1),
    gap.rem(1),
    width.percent(100),
    backgroundColor("#f6f6f6"),
    children,
  )

/** The content column, capped to a comfortable reading width. */
def pageColumn(children: List[HtmlElement]): HtmlElement =
  div(
    display.flex,
    flexDirection.column,
    gap.rem(1),
    width.percent(100),
    maxWidth.px(700),
    children,
  )

def pageTitle(title: String): HtmlElement =
  h1(fontSize.rem(2), fontWeight.bold, color("#22272d"), title)

def listColumn(children: List[HtmlElement]): HtmlElement =
  div(display.flex, flexDirection.column, gap.rem(0.5), children)

/** A tappable row that navigates to another page. */
def linkRow(name: String, page: Page): HtmlElement =
  a(
    Router.navigateTo(page),
    display.flex,
    alignItems.center,
    justifyContent.spaceBetween,
    padding.rem(1),
    borderRadius.rem(0.5),
    backgroundColor("#ffffff"),
    border("1px solid #dddddd"),
    color("#22272d"),
    textDecoration("none"),
    cursor.pointer,
    span(fontSize.rem(1.1), name),
    span(color("#999999"), ">"),
  )
