package net.systemvi.main_menu.client.views

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.mainmenu.common.dto.UserDto
import net.systemvi.main_menu.client.api.AuthApi
import net.systemvi.main_menu.client.components.{pageColumn, pageShell, pageTitle}
import net.systemvi.main_menu.client.routes.Pages.*
import net.systemvi.main_menu.client.routes.Router

/** Shared sign-in page: admins, waiters and customers all sign in here. */
def LoginPageView(): HtmlElement = {
  val session: Signal[Option[Option[UserDto]]] = AuthApi.session.toSignal(None)

  pageShell(List(
    pageColumn(List(
      pageTitle("Sign in"),
      div(child <-- session.map {
        case None =>
          div(color("#666666"), "Checking your session…")
        case Some(Some(_)) =>
          // Already signed in: no reason to show the button again.
          div(Router.navigateTo(WelcomePage))
        case Some(None) =>
          googleSignInButton
      }),
    ))
  ))
}

/** The Google "G" logo as an inline SVG data URI, so no external asset is needed. */
private val GoogleLogo: String =
  "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48'%3E%3Cpath fill='%23EA4335' d='M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z'/%3E%3Cpath fill='%234285F4' d='M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z'/%3E%3Cpath fill='%23FBBC05' d='M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z'/%3E%3Cpath fill='%2334A853' d='M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z'/%3E%3C/svg%3E"

private def googleSignInButton: HtmlElement = {
  val hover = Var(false)

  a(
    href(AuthApi.googleSignInUrl),
    display.flex,
    alignItems.center,
    justifyContent.center,
    gap.rem(0.5),
    width.rem(16),
    height.rem(2.75),
    borderRadius.rem(0.25),
    backgroundColor <-- hover.signal.map(if _ then "#f1f3f4" else "#ffffff"),
    border("1px solid #dadce0"),
    boxShadow <-- hover.signal.map(if _ then "0 1px 2px rgba(0,0,0,0.2)" else "none"),
    color("#3c4043"),
    textDecoration("none"),
    cursor.pointer,
    onMouseEnter --> { _ => hover.set(true) },
    onMouseLeave --> { _ => hover.set(false) },
    img(src(GoogleLogo), alt("Google logo"), width.px(18), height.px(18)),
    span(fontSize.rem(0.95), fontWeight("500"), "Sign in with Google"),
  )
}
