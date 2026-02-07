package net.systemvi.website.darkproject.slider

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.EventListener
import net.systemvi.website.styles.CSSProps.*
import net.systemvi.website.darkproject.Theme
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
    fontSize.rem:=2,
    color.gray,
   span(
      transform:="translateY(-0.125rem)",
      display.block,
      side match {
        case Side.Right=> "->"
        case Side.Left => "<-"
      }
    )
  )
}

private def Indicator(count:Int,selected:Var[Int]):Element={
  div(
    position.absolute,
    bottom.rem:=3,
    left.percent:=50,
    transform:="translateX(-50%)",
    display.flex,
    gap:="0.75rem",
    for(i<-0 until count)yield{
      div(
        cursor.pointer,
        width:="0.5rem",
        height:="0.5rem",
        borderRadius.percent:=50,
        transition:="300ms",
        onClick.map(_=>i)-->selected,
        backgroundColor<--selected.signal.map{selected => if selected != i then "rgb(220,220,220)" else "rgb(51,51,51)"}
      )
    }
  )
}

def ImageSlider(images:List[String]=List()):HtmlElement = {
  val currentlySelected=Var(0)
  div(
    width.percent:=100,
    height.px:=500,
    position.relative,
    div(
      Theme.common.roundedXL,
      width.percent:=100,
      height.px:=500,
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
      Indicator(images.length,currentlySelected)
    )
  )
}