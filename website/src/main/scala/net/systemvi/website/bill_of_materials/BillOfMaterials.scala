package net.systemvi.website.bill_of_materials

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.CSSProps.*

def BillOfMaterials():HtmlElement ={
  div(
    display.flex,
    padding.rem:=1,
    height.rem:=20,
    gap.rem:=1,
    div(
      flex:="1",
      position.relative,
      display.flex,
      flexDirection.column,
      overflowY.auto,
      hiddenScrollbar,
      gap:="10px",
      List.range(0,20).map(_=>div(
        div(
          paddingLeft.rem:=2,
          paddingRight.rem:=2,
          paddingTop.rem:=1,
          paddingBottom.rem:=1,
          backgroundColor.rgb(246,246,246),
          borderRadius.rem:=1,
          color.gray,
          fontSize.rem:=2,
          fontWeight:="500",
          "Asdfsdf Asdf SDF GSDG"
        )
      )),

    ),
    div(
      flex:="1",
      backgroundColor.rgb(246,246,246),
      borderRadius.rem:=1,
      color.gray,
    )
  )
}