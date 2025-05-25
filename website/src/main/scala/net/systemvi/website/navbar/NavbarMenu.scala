package net.systemvi.website.navbar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveElement

extension(signal:Signal[Boolean]){
  def inverse:Signal[Boolean]=signal.map(!_)
}

def NavbarMenu():List[Element]={
  val expanded=Var(false)
  List(
    button("Menu",onClick --> {_=> expanded.update(!_)} ),
    div(
      position:="fixed",
      top:="0",
      right:="0",
      display<--expanded.signal.map{if _ then "flex" else "none"},
      span("Keyboards"),
      span("Games"),
      span("Engine")
    )
  )
}
