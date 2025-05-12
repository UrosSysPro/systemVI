package net.systemvi.website.section

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.style.Theme

def SectionItemCard(item:SectionItem):Element=div(
  className:="flex flex-col gap-3",
  div(
    Theme.common.roundedXL,
    Theme.common.overflowHidden,
    img(
      src:=item.image,
      alt:=item.image
    ),
  ),
  div(
    className:="font-bold pl-3 text-xl",
    item.name
  ),
)
