package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.*

case class Card(icon: String, background: String)

private def CardComponent(card: Card): HtmlElement = {
  div(
    width.rem(4), height.rem(6),
    boxShadow("0 0 10px 0px black"),
    background("white"),
    backgroundClip.borderBox,
    padding.rem(0.25),
  )
}


def CatanCardsPageView: HtmlElement = {
  val icons = List(
    "public/images/catan/brick96x96.png",
    "public/images/catan/sheep100x100.png",
    "public/images/catan/stone100x100.png",
    "public/images/catan/wheat100x100.png",
    "public/images/catan/wood96x96.png",
  )

  val backgrounds = List.fill(icons.length){""}

  val cards = icons
    .zip(backgrounds)
    .map(Card.apply)


  div(
    display.flex, justifyContent.center, alignItems.center, gap.rem(3),
    cards.map{ card =>
      CardComponent(card)
    }
  )
}
