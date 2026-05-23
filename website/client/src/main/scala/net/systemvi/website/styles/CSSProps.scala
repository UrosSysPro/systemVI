package net.systemvi.website.styles

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.codecs
import com.raquo.laminar.modifiers.CompositeKeySetter
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base

import scala.io.Codec

object CSSProps {
  val objectFit: StyleProp[String]                                = styleProp[String]("object-fit")

  val hiddenScrollbar: CompositeKeySetter[HtmlAttr[String], Base] = cls := "hiddenScrollbar"

  val backdropFilter: StyleProp[String]                           = styleProp[String]("backdrop-filter")

  val aspect: StyleProp[String]                                   = styleProp[String]("aspect-ratio")

  val gridTemplateAreas:StyleProp[String]                         = styleProp[String]("grid-template-areas")

  val gridArea:StyleProp[String]                                  = styleProp[String]("grid-area")

  val referrerPolicy:HtmlAttr[String]                             = htmlAttr[String]("referrer-policy",codecs.StringAsIsCodec)
}