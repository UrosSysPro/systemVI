package net.systemvi.website.client.darkproject.neo_navbar

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.*
import net.systemvi.website.client.routes.Router
import net.systemvi.website.client.routes.Pages.*
import net.systemvi.website.client.styles.CSSProps.*
import net.systemvi.website.client.utils.Constants
import org.scalajs.dom

private object NeoNavbarDefaults {
  val TransitionDuration: String = "300ms"

  // Disable linter for magic numbers below if desired; kept here for clarity.
  val CollapseScrollThreshold: Int = 100
  val ExpandScrollThreshold: Int  = 200
  val MobileBreakpoint: Int       = 1000
  val CollapsedHeightRem: Int     = 4
  val ExpandedHeightRem: Int      = 6
  val MaxWidthPx: String          = "1450px"

  val ExpandedBackground: String  = "rgba(200,200,200,0.5)"
  val CollapsedBackground: String = "#f6f6f6"
  val HoverBackground: String     = "rgba(200,200,200,0.3)"
  val LinkColor: String           = "#555"
  val LinkHoverColor: String      = "#000"
}

private def Logo(): HtmlElement = {
  a(
    display.flex,
    justifyContent.center,
    alignItems.center,
    height.percent(100),
    paddingLeft.rem(1),
    img(
      src(s"${Constants.clientUrl}/images/logo/Logo.svg"),
      alt(""),
      display.block,
      width.rem(2)
    ),
    Router.navigateTo(HomePage)
  )
}

// Official Google "G" logo as an inline SVG data URI (no external asset required).
private val GoogleGLogo: String =
  "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48'%3E%3Cpath fill='%23EA4335' d='M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z'/%3E%3Cpath fill='%234285F4' d='M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z'/%3E%3Cpath fill='%23FBBC05' d='M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z'/%3E%3Cpath fill='%2334A853' d='M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z'/%3E%3C/svg%3E"

private def GoogleSignInLink(): HtmlElement = {
  val hover = Var[Boolean](false)
  a(
    href(s"${Constants.serverUrl}/auth/google/redirect"),
    display.flex,
    justifyContent.center,
    alignItems.center,
    gap.rem(0.5),
    width.rem(12),
    height.rem(2.5),
    paddingLeft.rem(1),
    paddingRight.rem(1),
    borderRadius.rem(.25),
    backgroundColor <-- hover.signal.map(if _ then "#f1f3f4" else "#ffffff"),
    border("1px solid #dadce0"),
    boxShadow <-- hover.signal.map(if _ then "0 1px 2px rgba(0,0,0,0.2)" else "none"),
    transition(NeoNavbarDefaults.TransitionDuration),
    color("#3c4043"),
    textDecoration("none"),
    cursor.pointer,
    onMouseEnter --> { _ => hover.set(true) },
    onMouseLeave --> { _ => hover.set(false) },
    img(
      src(GoogleGLogo),
      alt("Google logo"),
      width.px(18),
      height.px(18)
    ),
    span(
      fontSize.rem(0.875),
      fontWeight("500"),
      "Sign in with Google"
    )
  )
}

private def LogInButton(): HtmlElement = {
  val showLogInPopUp = Var(false)

  div(
    display.flex, justifyContent.center, alignItems.center, padding.rem(0.5),
    position.relative,
    button(
      width.rem(6),
      height.percent(100),
      borderRadius.rem(.5),
      cursor.pointer,
      backgroundColor := "transparent",
      border("none"),
      color("#555"),
      fontWeight("500"),
      "Log in"
    ),
    onClick --> {_=> showLogInPopUp.update(!_)},
    div(
      display <-- showLogInPopUp.signal.map(if _ then "flex" else "none"),
      position.absolute, top.percent(100), right.percent(0),
      flexDirection.column,
      alignItems.flexEnd,
      gap.rem(0.5),
      padding.rem(1),
      backgroundColor("white"),
      borderRadius.rem(.5),
      boxShadow("0 0.25rem 1rem rgba(0,0,0,0.15)"),
      GoogleSignInLink()
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
    transition(NeoNavbarDefaults.TransitionDuration),
    cursor.pointer,
    onMouseEnter --> { _ => hover.set(true) },
    onMouseLeave --> { _ => hover.set(false) },
    color <-- hover.signal.map {
      if _ then NeoNavbarDefaults.LinkHoverColor else NeoNavbarDefaults.LinkColor
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
      `type` := "button",
      onClick --> { _ => slideInNavbarExpanded.set(true) },
      img(
        src(s"${Constants.clientUrl}/images/icons/hamburger-menu.svg"),
        alt("Open menu"),
      )
    )
  )
}

private def SlideInNavbar(slideInNavbarExpanded: Var[Boolean], navbarLinks: List[(String,Page)]): HtmlElement = {
  div(
    position.fixed, top.percent(0), left.percent(0), width.vw(100),height.vh(100), zIndex(2),
    transition(NeoNavbarDefaults.TransitionDuration),
    backgroundColor <-- slideInNavbarExpanded.signal.map{ if _ then "rgba(50,50,50,0.3)" else "rgba(50,50,50,0)"},
    pointerEvents <-- slideInNavbarExpanded.signal.map{ if _ then "all" else "none" },
    onClick --> { _ => slideInNavbarExpanded.set(false) },
    div(
      onClick --> { _.stopPropagation() },
      position.absolute, top.percent(0),
      transition(NeoNavbarDefaults.TransitionDuration),
      right.percent <-- slideInNavbarExpanded.signal.map(if _ then 0 else -100),
      width.rem(20), height.vh(100),
      backgroundColor("white"),

      div(
        display.flex, alignItems.center, justifyContent.start,
        padding.rem(1),
        gap.rem(1),
        img(
          src(s"${Constants.clientUrl}/images/logo/Logo.svg"),
          alt(""),
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
            onMouseEnter --> { _ => hover.set(true) },
            onMouseLeave --> { _ => hover.set(false) },
            a(
              display.block,
              paddingLeft.rem(2),
              paddingRight.rem(2),
              paddingTop.rem(1),
              paddingBottom.rem(1),
              transition(NeoNavbarDefaults.TransitionDuration),
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

private def navbarLinks: List[(String, Page)] = List(
  ("Configurator",  ConfiguratorPage),
  ("Keyboards",     KeyboardsPage),
  ("Games",         GamesPage),
  ("Engine",        EnginePage),
  ("3D Printing",   ThreeDPrintingPage),
  ("Knitting",      KnittingPage),
  ("Origami",       OrigamiPage),
)

def NeoNavbar(): List[Modifier[HtmlElement]] = {
  import NeoNavbarDefaults.*

  val expanded = windowEvents(_.onScroll).map { _ => dom.window.scrollY.toInt > ExpandScrollThreshold }.startWith(dom.window.scrollY.toInt > ExpandScrollThreshold)
  val showMenu = windowEvents(_.onResize).map { _ => dom.window.innerWidth < MobileBreakpoint }.startWith(dom.window.innerWidth < MobileBreakpoint)
  val slideInNavbarExpanded = Var[Boolean](false)

  List(
    div(
      position.fixed, left.percent(50), top.percent(0), zIndex(1), transform("translateX(-50%)"),

      width.percent(100),
      height.rem <-- expanded.map(if _ then CollapsedHeightRem else ExpandedHeightRem),

      transition(TransitionDuration),

      maxWidth <-- expanded.map(if _ then "100%" else MaxWidthPx),
      padding.rem <-- expanded.map(if _ then 0 else 1),

      div(
        display.flex,
        flexDirection.row,
        justifyContent.spaceBetween,
        width.percent(100), height.percent(100),
        transition(TransitionDuration),

        borderRadius.rem <-- expanded.map(if _ then 0 else 1),
        backgroundColor <-- expanded.map(if _ then ExpandedBackground else CollapsedBackground),
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
            LogInButton(),
          )
        }
      )
    ),
    child <-- showMenu.map{ if _ then SlideInNavbar(slideInNavbarExpanded,navbarLinks) else emptyNode},
  )
}
