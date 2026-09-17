package net.systemvi.website.views

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom.*
import org.scalajs.dom

import scalajs.*
import scalajs.js.*
import io.circe.*
import io.circe.generic.auto.*
import io.circe.scalajs.*

import scala.scalajs.js.JSConverters.JSRichIterableOnce


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
//    boxShadow("0 0 10px 0px black"),
    borderRadius.rem(1),
    border("0.25rem solid #222"),
    overflow.hidden,
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
    "images/catan/brick96x96.png",
    "images/catan/sheep100x100.png",
    "images/catan/stone100x100.png",
    "images/catan/wheat100x100.png",
    "images/catan/wood96x96.png",
    "images/catan/knight.png",
    "images/catan/plusOne.png",
    "images/catan/collect.png",
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

  val resources = cards.take(5)
  val utility = cards.slice(5, 8)
  console.log(resources.asJsAny,utility.asJsAny)

  val gridGap = 0.25f

  div(
    display.flex,
    flexDirection.column,
    alignItems.center,
    gap.rem(gridGap),
    padding.rem(gridGap),
    resources.map{ card =>
      div(
        display.flex,
        flexDirection.column,
        gap.rem(gridGap),
        List.range(0,5).map{_=>
          div(
            display.flex,
            flexDirection.row,
            gap.rem(gridGap),
            List.range(0, 6).map { _ =>
              CardComponent(card)
            }
          )
        }
      )
    },

    utility.zipWithIndex.map{ (card,i) =>
      div(
        display.flex,
        flexDirection.column,
        gap.rem(gridGap),
        List.range(0, if i==0 then 3 else 2).map{_=>
          div(
            display.flex,
            flexDirection.row,
            gap.rem(gridGap),
            List.range(0, 6).map { _ =>
              CardComponent(card)
            }
          )
        }
      )
    },

  )
}
