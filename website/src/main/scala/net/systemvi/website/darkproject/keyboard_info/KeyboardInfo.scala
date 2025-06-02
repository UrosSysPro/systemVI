package net.systemvi.website.darkproject.keyboard_info

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom
import net.systemvi.website.CSSProps.*
import net.systemvi.website.darkproject.Theme
import net.systemvi.website.darkproject.expandable_specs.UnderlinedRow
import net.systemvi.website.model.*

private def KeyboardInfoLeft(keyboard:Keyboard):HtmlElement={
  div(
    flex := "1",
    height.percent := 100,
    Theme.common.roundedXL,
    overflow.hidden,
    display.flex,
    flexDirection.column,
    gap.rem := 1,
    div(
      flex := "1",
      Theme.common.roundedXL,
      overflow.hidden,
      img(
        width.percent := 100,
        height.percent := 100,
        objectFit := "cover",
        src := keyboard.images(0),
        alt := keyboard.images(0)
      )
    ),
    div(
      display.flex,
      height.rem:=5,
      overflowX.auto,
      gap:="0.5rem",
      hiddenScrollbar,
      keyboard.images.map{ url=>img(
        src:=url,
        alt:=url,
        borderRadius.rem:=1,
      )}
    ),
  )
}

private def KeyboardInfoRight(keyboard:Keyboard)={
  div(
    display.flex,
    flexDirection.column,
    flex := "1",
    height.percent := 100,
    Theme.common.roundedXL,
    backgroundColor := "#f6f6f6",
    overflow.hidden,
    padding.rem := 1,
    span(
      paddingBottom.rem := 1,
      color := "#9e9e9e",
      fontSize := "0.75rem",
      keyboard.codeName
    ),
    span(
      paddingBottom.rem := 2,
      color := "#22272d",
      fontSize := "1.25rem",
      fontWeight.bolder,
      keyboard.name
    ),
    span(
      paddingBottom.rem := 1,
      color := "#22272d",
      fontSize.rem := 1,
      fontWeight.bold,
      "Product Specs"
    ),
    for (spec <- keyboard.specs) yield UnderlinedRow(spec.name,spec.value)
  )
}

def KeyboardInfo(keyboard:Keyboard):Element = {
  val horizontal=windowEvents(_.onResize).map(_=>dom.window.innerWidth>1000).startWith(dom.window.innerWidth>1000)
  div(
    height.rem:=40,
    display.flex,
    gap.rem:=2,
    paddingLeft.rem:=1,
    paddingRight.rem:=1,
    flexDirection<--horizontal.map(if _ then "row" else "column"),

    KeyboardInfoLeft(keyboard),
    KeyboardInfoRight(keyboard),
  )
}
