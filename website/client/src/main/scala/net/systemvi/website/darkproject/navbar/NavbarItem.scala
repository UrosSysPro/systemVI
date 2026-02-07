package net.systemvi.website.darkproject.navbar

import com.raquo.laminar.api.L.{*, given}
import net.systemvi.website.routes.Router

def NavbarItem(entry:NavbarEntry):Element = a(
  Router.navigateTo(entry.page),
  entry.text,
  className := "hover:text-black transition cursor-pointer"
)
