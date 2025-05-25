package net.systemvi.website.navbar

import com.raquo.laminar.api.L.{*, given}

def NavbarItem(text:String):Element = span(
  text,
  className := "hover:text-black transition cursor-pointer"
)
