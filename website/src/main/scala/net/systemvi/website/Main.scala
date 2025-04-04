package net.systemvi.website

import scala.scalajs.js
import scala.scalajs.js.annotation.*
import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

@main
def LiveChart(): Unit = {
  renderOnDomContentLoaded(
    dom.document.getElementById("app"),
    Main.appElement()
  )
}

object Main {
  def appElement(): Element = Page()
}