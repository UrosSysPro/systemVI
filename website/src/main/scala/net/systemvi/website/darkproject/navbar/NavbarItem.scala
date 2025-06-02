package net.systemvi.website.darkproject.navbar

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.router

def NavbarItem(entry:NavbarEntry):Element = a(
  router.navigateTo(entry.page),
  entry.text,
  className := "hover:text-black transition cursor-pointer"
)
