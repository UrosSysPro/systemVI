package net.systemvi.website.navbar

import com.raquo.laminar.api.L.{*, given}

def NavbarItem(text:String):Element = span(
  text,
  className := "hover:text-black transition cursor-pointer"
)

def Navbar():Element = div(
  className:="w-full h-24 px-4 py-4",
  div(
    className:="w-full h-full bg-[#f6f6f6] rounded-[16px] flex justify-between items-center px-6 text-[#626569] text-xl",
    NavbarItem("Logo"),
    div(
      className:="flex gap-4",
      NavbarItem("Keyboards"),
      NavbarItem("Games"),
      NavbarItem("Engine")
    )
  )
)