package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.*

case class Card(icon: String, background: String, color: String, name: String)

private def CardComponent(card: Card): HtmlElement = {

  def title(mods: Modifier[HtmlElement]*)={
    div(
      height.rem(4),
      background(card.color),
      display.flex, alignItems.center,
      paddingLeft.rem(1),
      fontWeight("bold"),
      color("white"),
      fontSize.rem(1.5),
      card.name,
      mods
    )
  }

  div(
    width.rem(10), height.rem(16),
    boxShadow("0 0 10px 0px black"),
    background("white"),
    backgroundClip.borderBox,
    display.flex, flexDirection.column,
    title(),
    div(
      flex("1"),
      display.flex, justifyContent.center, alignItems.center,
      img(
        src(card.icon)
      )
    ),
    title(transform("rotate(180deg)")),
  )
}


def CatanCardsPageView: HtmlElement = {
  val icons = List(
    "public/images/catan/brick96x96.png",
    "public/images/catan/sheep100x100.png",
    "public/images/catan/stone100x100.png",
    "public/images/catan/wheat100x100.png",
    "public/images/catan/wood96x96.png",
    "public/images/catan/knight.png",
    "public/images/catan/plusOne.png",
    "public/images/catan/collect.png",
  )

  val backgrounds = List.fill(icons.length){""}

  val colors = List(
    "#f67441",
    "green",
    "#75849d",
    "#f9b621",
    "darkgreen",
    "#c4939c",
    "#a2a2cc",
    "#222",
  )
  val names = List(
    "Brick",
    "Sheep",
    "Stone",
    "Wheat",
    "Wood",
    "Knight",
    "+1 Point",
    "Collect All",
  )

  val cards: List[Card] = for(i<-icons.indices.toList)
    yield Card(
      icons(i),
      backgrounds(i),
      colors(i),
      names(i),
    )

  div(
    display.flex, justifyContent.center, alignItems.center, gap.rem(3),
    height.vh(100),
    cards.map{ card =>
      CardComponent(card)
    }
  )
}
