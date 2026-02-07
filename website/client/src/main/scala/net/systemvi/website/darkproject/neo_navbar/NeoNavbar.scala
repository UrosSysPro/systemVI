package net.systemvi.website.darkproject.neo_navbar

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.*
import net.systemvi.website.CSSProps.*
import net.systemvi.website.routes.Pages.*
import net.systemvi.website.routes.Router
import org.scalajs.dom

private def Logo(): HtmlElement = {
  a(
    display.flex,
    justifyContent.center,
    alignItems.center,
    height.percent(100),
    paddingLeft.rem(1),
    img(
      src(s"${Constants.clientUrl}/images/logo/Logo.svg"),
      alt("Logo.svg"),
      display.block,
      width.rem(2), width.rem(2)
    ),
    Router.navigateTo(HomePage)
  )
}

private def ContactUs(): HtmlElement = {
  val hover = Var[Boolean](false)
  div(
    display.flex,
    justifyContent.center,
    alignItems.center,
    height.percent(100),
    paddingRight.rem(1),
    button(
      display.flex,
      justifyContent.center,
      alignItems.center,
      paddingLeft.rem(1),
      paddingRight.rem(1),
      paddingTop.rem(0.5f),
      paddingBottom.rem(0.5f),
      borderRadius.rem(0.3f),
      transition("300ms"),
      onMouseEnter --> { _ => hover.writer.onNext(true) },
      onMouseLeave --> { _ => hover.writer.onNext(false) },
      opacity <-- hover.signal.map {
        if _ then "0.8" else "1"
      },
      backgroundColor("#22272d"),
      color("white"),
      cursor.pointer,
      "Contact Us",
    )
  )
}

private def Link(name: String, page: Page): HtmlElement = {
  val hover = Var[Boolean](false)
  a(
    display.flex,
    justifyContent.center,
    alignItems.center,
    paddingLeft.rem(0.5),
    paddingRight.rem(0.5),
    height.percent(100),
    Router.navigateTo(page),
    transition("300ms"),
    cursor.pointer,
    onMouseEnter --> { _ => hover.writer.onNext(true) },
    onMouseLeave --> { _ => hover.writer.onNext(false) },
    color <-- hover.signal.map {
      if _ then "#000" else "#555"
    },
    name
  )
}

private def ShowMenuButton(slideInNavbarExpanded: Var[Boolean]): HtmlElement = {
  div(
    display.flex, justifyContent.center, alignItems.center,
    height.percent(100),
    button(
      display.flex, justifyContent.center, alignItems.center,
      padding.rem(1),
      cursor.pointer,
      onClick --> { _ => slideInNavbarExpanded.writer.onNext(true) },
      img(
        src(s"${Constants.clientUrl}/images/icons/hamburger-menu.svg"),
        alt("hamburger-menu.svg"),
      )
    )
  )
}

private def SlideInNavbar(slideInNavbarExpanded: Var[Boolean], navbarLinks: List[(String,Page)]): HtmlElement = {
  div(
    position.fixed, top.percent(0), left.percent(0), width.vw(100),height.vh(100), zIndex(2),
    transition("300ms"),
    backgroundColor <-- slideInNavbarExpanded.signal.map{ if _ then "rgba(50,50,50,0.3)" else "rgba(50,50,50,0)"},
    pointerEvents <-- slideInNavbarExpanded.signal.map{ if _ then "all" else "none" },
    onClick --> { _ => slideInNavbarExpanded.writer.onNext(false) },
    div(
      onClick --> { _.stopPropagation() },
      position.absolute, top.percent(0), right.percent(0),
      transition("300ms"),
      right.percent <-- slideInNavbarExpanded.signal.map(if _ then 0 else -100),
      width.rem(20), height.vh(100),
      backgroundColor("white"),

      div(
        display.flex, alignItems.center, justifyContent.start,
        padding.rem(1),
        gap.rem(1),
        img(
          src(s"${Constants.clientUrl}/images/logo/Logo.svg"),
          alt("Logo.svg"),
        ),
        span(
          fontSize.rem(1.5f),
          "System VI",
        )
      ),
      hr(),
      div(
        display.flex, flexDirection.column,
        navbarLinks.map{ case (name, page) =>
          val hover = Var[Boolean](false)
          div(
            width.percent(100),
            paddingLeft.rem(1),
            paddingRight.rem(1),
            paddingTop.rem(0.5f),
            paddingBottom.rem(0.5f),
            onMouseEnter --> { _ => hover.writer.onNext(true) },
            onMouseLeave --> { _ => hover.writer.onNext(false) },
            a(
              display.block,
              paddingLeft.rem(2),
              paddingRight.rem(2),
              paddingTop.rem(1),
              paddingBottom.rem(1),
              transition("300ms"),
              borderRadius.rem(1),
              width.percent(100),
              backgroundColor <-- hover.signal.map{ if _ then "rgba(200,200,200,0.3)" else "rgba(200,200,200,0.0)" },
              Router.navigateTo(page),
              name
            )
          )
        }
      )
    )
  )
}

def NeoNavbar(): List[Modifier[HtmlElement]] = {
  val expanded = windowEvents(_.onScroll).map { _ => dom.window.scrollY.toInt > 200 }.startWith(dom.window.scrollY.toInt > 100)
  val showMenu = windowEvents(_.onResize).map { _ => dom.window.innerWidth < 1000 }.startWith(dom.window.innerWidth < 1000)
  val slideInNavbarExpanded = Var[Boolean](false)

  val navbarLinks = List(
    ("Configurator",  ConfiguratorPage),
    ("Keyboards",     KeyboardsPage),
    ("Games",         GamesPage),
    ("Engine",        EnginePage),
    ("3D Printing",   ThreeDPrintingPage),
    ("Knitting",      KnittingPage),
    ("Origami",       OrigamiPage),
  )

  List(
    div(
      position.fixed, left.percent(50), top.percent(0), zIndex(1), transform("translateX(-50%)"),

      width.percent(100),
      height.rem <-- expanded.map(if _ then 4 else 6),

      transition("300ms"),

      maxWidth <-- expanded.map(if _ then "100%" else "1450px"),
      padding.rem <-- expanded.map(if _ then 0 else 1),

      div(
        display.flex,
        flexDirection.row,
        justifyContent.spaceBetween,
        width.percent(100), height.percent(100),
        transition("300ms"),

        borderRadius.rem <-- expanded.map(if _ then 0 else 1),
        backgroundColor <-- expanded.map(if _ then "rgba(200,200,200,0.5)" else "#f6f6f6"),
        backdropFilter <-- expanded.map(if _ then "blur(3rem)" else "blur(0)"),

        children <-- showMenu.map{
          if _ then List(
            Logo(),
            ShowMenuButton(slideInNavbarExpanded),
          ) else List(
            Logo(),
            div(
              display.flex,
              flexDirection.row,
              justifyContent.center,
              navbarLinks.map{ case (name, page) =>
                Link(name, page)
              }
            ),
            ContactUs(),
          )
        }
      )
    ),
    child <-- showMenu.map{ if _ then SlideInNavbar(slideInNavbarExpanded,navbarLinks) else emptyNode},
  )
}
