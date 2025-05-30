package net.systemvi.website.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.style.Theme
import net.systemvi.website.{KeyboardsPage, router}

def SectionItemCard(item:SectionItem):Element={
  val hover=Var(false)
  a(
    router.navigateTo(item.page),
    className:="flex flex-col gap-3",
    onMouseEnter-->{_=>hover.set(true)},
    onMouseLeave-->{_=>hover.set(false)},
    cursor.pointer,
    div(
      Theme.common.roundedXL,
      Theme.common.overflowHidden,
      position.relative,
      img(
        src:=item.image,
        alt:=item.image
      ),
      div(
        transition:="300ms",
        top.rem:=0,
        left.rem:=0,
        right.rem:=0,
        height.percent:=100,
        position.absolute,
        opacity<--hover.signal.map(if _ then 1 else 0),
        backgroundColor.rgba(51,51,51,0.2)
      )
    ),
    div(
      className:="font-bold pl-3 text-xl",
      item.name
    ),
  )
}
