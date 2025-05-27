package net.systemvi.website.navbar

import com.raquo.laminar.api.L.{*, given}

def NavbarItem(entry:NavbarEntry):Element = a(
  href:=entry.url,
  entry.text,
  className := "hover:text-black transition cursor-pointer"
)
