package net.systemvi.website.client.views.profile

import cats.*
import cats.implicits.*
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import io.circe.scalajs.given
import io.circe.generic.auto.*
import io.circe.generic.*
import net.systemvi.website.client.darkproject.footer.Footer
import net.systemvi.website.client.darkproject.neo_navbar.NeoNavbar
import net.systemvi.website.client.services.UserService
import net.systemvi.website.client.styles.CSSProps.referrerPolicy


def UserProfilePageView():HtmlElement = {
  val user = UserService.whoAmI

  div(
    display.flex, flexDirection.column, alignItems.center, paddingTop.rem(6),
    div(
      display.flex, flexDirection.column, justifyContent.start, alignItems.center,
      width.percent(100), maxWidth.px(1450),
      NeoNavbar(),
      div(
        display.flex, flexDirection.column, alignItems.center,
        img(
          src <-- user.map(_.picture.getOrElse("#")),
          alt("profile picutre"),
          referrerPolicy("no-referrer"),
          width.rem(10), height.rem(10), borderRadius.percent(100),
        ),
        h1(
          text <-- user.map(_.name)
        ),
        h1(
          text <-- user.map(_.email)
        ),
      ),
      Footer(),
    ),
  )
}
