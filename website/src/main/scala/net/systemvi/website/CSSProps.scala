package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.CompositeKeySetter
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base

object CSSProps {
  val objectFit: StyleProp[String] = styleProp[String]("object-fit")
  val hiddenScrollbar: CompositeKeySetter[HtmlAttr[String], Base] =cls:="hiddenScrollbar"
}