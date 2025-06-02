package net.systemvi.website.darkproject.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.{KeyboardsPage, router}
import net.systemvi.website.CSSProps.*
import net.systemvi.website.darkproject.Theme

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
      aspect:="4/3",
      img(
        src:=item.image,
        alt:=item.image,
        objectFit:="cover",
        width.percent:=100,
        height.percent:=100,
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
      className:="pl-3 text-xl",
      item.name
    ),
  )
}
