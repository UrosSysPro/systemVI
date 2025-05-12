package net.systemvi.website.slider

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.style.Theme

def Image(url:String):Element=div(
  cls:="flex-1 h-full",
  img(
    cls:="object-cover w-full h-full",
    src:=url,
    alt:=url,
  )
)

def ImageSlider(images:List[String]=List()):Element = div(
  className:="w-full h-[500px] px-4 py-4",
  div(
    Theme.common.roundedXL,
    className:="w-full h-full overflow-x-auto",
    div(
      width:=s"${images.length*100}%",
      className:="h-full flex flex-row",
      images.map(Image)
    )
  )
)