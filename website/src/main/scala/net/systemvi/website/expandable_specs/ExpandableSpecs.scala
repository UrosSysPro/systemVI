package net.systemvi.website.expandable_specs

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.model.{Keyboard, KeyboardSpec}
import org.scalajs.dom

private def ExpandableSpecsItem(keyboardSpec: KeyboardSpec):HtmlElement={
  val opened=Var(false)
  div(
    backgroundColor.rgb(246, 246, 246),
    borderRadius.rem := 1,
    display.flex,
    flexDirection.column,
    paddingLeft.rem := 2,
    paddingRight.rem := 2,
    paddingTop.rem := 1,
    paddingBottom.rem := 1,
    onClick-->{_=>opened.update(!_)},
    div(
      display.flex,
      alignItems.center,
      span("01", width.percent := 30,fontSize:="1.5rem",color.gray,fontWeight:="500"),
      span("Name", flex := "1",fontSize.rem:=2,color.gray,fontWeight:="500"),
      span(">",transform<--opened.signal.map(if _ then "rotateZ(90deg)" else "rotateZ(-90deg)"),transition:="300ms")
    ),
    child<--opened.signal.map:
      if _ then
        div(
          List.range(0 , 5).map{_=>div(
          display.flex,
          div(width.percent:=30),
          UnderlinedRow("aaaaa","aaaaaaaa").amend(flex:="1")
        )},
          paddingBottom:="0.5rem",
          paddingTop:="0.25rem",
        )
      else
        emptyNode,
  )
}

def ExpandableSpecs(keyboard:Keyboard):HtmlElement={
  div(
    display.flex,
    flexDirection.column,
    padding.rem:=1,
    gap:="0.5rem",
    keyboard.specs.map(ExpandableSpecsItem)
  )
}