package net.systemvi.website.big_title

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

def BigTitle(title:String,sideText:String=""):Element={
  val textSize=windowEvents(_.onResize).map(_=>if dom.window.innerWidth>500 then 6 else 3).startWith(if dom.window.innerWidth>500 then 6 else 3)

  div(
    display.flex,
    justifyContent.spaceBetween,
    alignItems.start,
    padding.rem:=1,
    h1(
      flex:="1",
      fontSize.rem<--textSize,
      color:="#22272d",
      fontWeight.bold,
      title
    ),
    if sideText.isEmpty then emptyNode else span(
      maxWidth.percent:=30,
      sideText
    )
  )
}
