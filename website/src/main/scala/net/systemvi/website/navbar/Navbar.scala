package net.systemvi.website.navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.api.textToTextNode
import net.systemvi.website.style.Theme
import org.scalajs.dom

def NavbarItem(text:String):Element = span(
  text,
  className := "hover:text-black transition cursor-pointer"
)

extension(signal:Signal[Boolean]){
  def inverse:Signal[Boolean]=signal.map(!_)
}

def Navbar():Element = {
  val expanded = windowEvents(_.onScroll).map { _ => dom.window.scrollY.toInt > 100 }.startWith(false)
  val showMenu = windowEvents(_.onResize).map { _ => dom.window.innerWidth < 1000 }.startWith(dom.window.innerWidth < 1000)

  div(
    className:="w-full  fixed left-1/2 top-0 -translate-x-1/2 flex flex-col items-center",
    (className:="max-w-[1450px] px-4 py-4 h-24") <-- expanded.inverse,
    (className:="max-w-full px-0 py-0 h-16")<--expanded,
    transition:="300ms",
    div(
      (className:="rounded-xl bg-[#f6f6f6]") <-- expanded.inverse,
      (className:="backdrop-blur bg-[rgba(200,200,200,0.5)]")<--expanded,

      className:="w-full h-full flex justify-between items-center px-6 text-[#626569] text-xl ",
      transition:="300ms",
      NavbarItem("Logo"),
      div(
        className:="flex gap-4",
        NavbarItem("Keyboards"),
        NavbarItem("Games"),
        NavbarItem("Engine")
      )
    )
  )
}