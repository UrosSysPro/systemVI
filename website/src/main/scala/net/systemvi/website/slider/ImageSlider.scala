package net.systemvi.website.slider

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.EventListener
import net.systemvi.website.style.Theme
import net.systemvi.website.CSSProps.*
import org.scalajs.dom.MouseEvent

private enum Side {
  case Left
  case Right
}

private def ArrowButton(side:Side,onClick:EventListener[MouseEvent,MouseEvent]):Element={
  button(
    onClick,
    position.absolute,
    side match {
      case Side.Left=>left.rem:=2
      case Side.Right=>right.rem:=2
    },
    top.percent := 50,
    transform := "translateY(-50%)",
    width.rem := 3,
    height.rem := 3,
    backdropFilter:="blur(10px)",
    backgroundColor.rgba(220,220,220,0.5),
    borderRadius.rem:=1,
    cursor.pointer,
  )
}
private def Indicator(count:Int,selected:Signal[Int]):Element={
  div(
    position.absolute,
    bottom.rem:=3,
    left.percent:=50,
    transform:="translateX(-50%)",
    display.flex,
    gap.rem:=2,
    for(i<-0 until count)yield{
      div(
        width.rem:=1,
        height.rem:=1,
        borderRadius.rem:=1,
        transition:="300ms",
        backgroundColor<--selected.map{selected => if selected != i then "rgb(220,220,220)" else "rgb(51,51,51)"}
      )
    }
  )
}

def ImageSlider(images:List[String]=List()):Element = {
  val currentlySelected=Var(0)
  div(
    width.percent:=100,
    height.px:=500,
    position.relative,
    div(
      Theme.common.roundedXL,
      width.percent:=100,
      height.px:=500,
//      overflowX.auto,
//      hiddenScrollbar,
      overflow.hidden,
      div(
        transition:="300ms",
        transform<--currentlySelected.signal.map{s=>
          s"translateX(-${s.toFloat*100/images.length}%)"
        },
        width.percent:=images.length*100,
        height.percent:=100,
        display.flex,
        images.map{url=>
          div(
            flex:="1",
            padding.rem:=1,
            img(
              Theme.common.roundedXL,
              height.percent:=100,
              width.percent:=100,
              objectFit:="cover",
              src := url,
              alt := url,
            )
          )
        }
      ),
      ArrowButton(Side.Left,onClick-->{_=>
        currentlySelected.update(s=>Math.max(s-1,0))
      }),
      ArrowButton(Side.Right,onClick-->{_=>
        currentlySelected.update(s=>Math.min(s+1,images.length-1))
      }),
      Indicator(images.length,currentlySelected.signal)
    )
  )
}