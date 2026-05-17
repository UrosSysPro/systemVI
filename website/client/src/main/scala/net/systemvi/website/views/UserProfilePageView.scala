package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement, console}
import org.scalajs.dom
import io.circe.scalajs.given
import io.circe.generic.auto.*
import io.circe.generic.*
import net.systemvi.website.services.UserService


def UserProfilePageView():HtmlElement = {
  val user = UserService.whoAmI
  div(
    text <-- user.map(user => s"${user}")
  )
}
