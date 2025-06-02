package net.systemvi.website.darkproject.bill_of_materials

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
      div(
        display.flex,
        flexDirection.column,
        overflowY.auto,
        hiddenScrollbar,
        gap:="10px",
        height.percent:=100,
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
        position.absolute,
        bottom.rem:=0,
        left.rem:=0,
        right.rem:=0,
        height.rem:=4,
        background:="linear-gradient(rgba(255,255,255,0), #fff)"
      )
    ),
    div(
      flex:="1",
      backgroundColor.rgb(246,246,246),
      borderRadius.rem:=1,
      color.gray,
    )
  )
}