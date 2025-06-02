package net.systemvi.website.darkproject.footer

import com.raquo.laminar.api.L.{*, given}

def FooterItem(text:String):Element = span(
  text,
  className := "hover:text-black transition cursor-pointer"
)

def Footer():Element = div(
  className:="w-full h-24 px-4 py-4",
  div(
    className:="w-full h-full bg-[#f6f6f6] rounded-[16px] flex justify-between items-center px-6 text-[#626569] text-xl",
    FooterItem("Logo"),
    div(
      className:="flex gap-4",
      FooterItem("About"),
      FooterItem("Useful links")
    )
  )
)