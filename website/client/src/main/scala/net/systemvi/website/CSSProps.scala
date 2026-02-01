package net.systemvi.website

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.modifiers.CompositeKeySetter
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base

object CSSProps {
  val objectFit: StyleProp[String]                                = styleProp[String]("object-fit")

  val hiddenScrollbar: CompositeKeySetter[HtmlAttr[String], Base] = cls := "hiddenScrollbar"

  val backdropFilter: StyleProp[String]                           = styleProp[String]("backdrop-filter")

  val aspect: StyleProp[String]                                   = styleProp[String]("aspect-ratio")

  val gridTemplateAreas:StyleProp[String]                         = styleProp[String]("grid-template-areas")

  val gridArea:StyleProp[String]                                  = styleProp[String]("grid-area")

//  def hover(props:List[Modifier[HtmlElement]]*) = List[Modifier[HtmlElement]]{
//    val hover = Var[Boolean](false)
//    List(
//      onMouseEnter --> { _ => hover.writer.onNext(true) },
//      onMouseLeave --> { _ => hover.writer.onNext(false) },
//       <-- hover.signal.map(if _ then props else List.empty),
//    )
//  }
}