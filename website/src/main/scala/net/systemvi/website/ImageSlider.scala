package net.systemvi.website

import com.raquo.laminar.api.L.{*,given}

def ImageSlider(images:List[String]=List()):Element = div(
  className:="w-full h-[500px] px-4 py-4",
  div(
    className:="w-full h-full rounded-[16px] bg-red-500 "
  )
)