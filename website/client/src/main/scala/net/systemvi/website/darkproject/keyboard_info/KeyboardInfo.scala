package net.systemvi.website.darkproject.keyboard_info

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.common.model.{Game, Keyboard, ProductSpec}
import net.systemvi.website.CSSProps.*
import net.systemvi.website.darkproject.Theme
import net.systemvi.website.darkproject.expandable_specs.UnderlinedRow
import org.scalajs.dom

private def ProductInfoLeft(images:List[String]):HtmlElement={
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
        src := images.head,
        alt := images.head,
      )
    ),
    div(
      display.flex,
      height.rem:=5,
      overflowX.auto,
      gap:="0.5rem",
      hiddenScrollbar,
      images.map{ url=>img(
        src:=url,
        alt:=url,
        borderRadius.rem:=1,
      )}
    ),
  )
}

private def ProductInfoRight(name:String,codeName:String,specs:List[ProductSpec])={
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
      codeName
    ),
    span(
      paddingBottom.rem := 2,
      color := "#22272d",
      fontSize := "1.25rem",
      fontWeight.bolder,
      name
    ),
    span(
      paddingBottom.rem := 1,
      color := "#22272d",
      fontSize.rem := 1,
      fontWeight.bold,
      "Product Specs"
    ),
    for (spec <- specs) yield UnderlinedRow(spec.name,spec.value)
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

    ProductInfoLeft(keyboard.images),
    ProductInfoRight(keyboard.name,keyboard.codeName,keyboard.specs),
  )
}
def GameInfo(game:Game):HtmlElement={
  val horizontal=windowEvents(_.onResize).map(_=>dom.window.innerWidth>1000).startWith(dom.window.innerWidth>1000)
  div(
    height.rem:=40,
    display.flex,
    gap.rem:=2,
    paddingLeft.rem:=1,
    paddingRight.rem:=1,
    flexDirection<--horizontal.map(if _ then "row" else "column"),

    ProductInfoLeft(game.images),
    ProductInfoRight(game.name,game.codeName,game.specs),
  )
}
