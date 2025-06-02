package net.systemvi.website.darkproject.navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveElement
import net.systemvi.website.router

extension(signal:Signal[Boolean]){
  def inverse:Signal[Boolean]=signal.map(!_)
}

def NavbarMenu(entries:List[NavbarEntry]):List[Element]={
  val menuOpened=Var(false)
  List(
    button(
      "Menu",
      onClick --> {_=> menuOpened.update(!_)},
      cursor.pointer,
      transition:="300ms",
      cls:="hover:text-black",
    ),

    div(
      position.fixed, top.px:=0, height.vh:=100, left.px:=0, right.px:=0,
      backgroundColor.rgba(51,51,51,0.2),
      display<--menuOpened.signal.map:
        if _ then "flex" else "none",
      onClick-->{_=>menuOpened.set(false)},
      //onScroll-->{event=>
       // event.preventDefault()
       // event.stopPropagation()
      //},
      div(
        position.absolute,
        display.flex,
        flexDirection.column,
        justifyContent.start,
        alignItems.start,
        top:="0",
        right:="0",
        left:="0",
        paddingBottom.rem:=2,
        paddingTop.rem:=1,
        backgroundColor.white,
        borderBottomLeftRadius.rem:=1,
        borderBottomRightRadius.rem:=1,
        paddingLeft.rem:=1,
        paddingRight.rem:=1,
        entries.map:
          item=>a(
            router.navigateTo(item.page),
            paddingTop.rem:=1,
            paddingBottom.rem:=1,
            paddingLeft.rem:=1,
            paddingRight.rem:=1,
            textDecoration.none,
            color.gray,
            width.percent:=100,
            borderRadius.rem:=1,
            transition:="300ms",
            cls:="hover:bg-gray-300",
            item.text
          )
      )
    )
  )
}
