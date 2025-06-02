package net.systemvi.website.darkproject.bento_box

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import net.systemvi.website.CSSProps.*
import org.scalajs.dom


case class BentoBoxSize(width:Int,height:Int)
case class BentoBoxRect(x:Int,y:Int,width:Int,height:Int)
case class BentoBoxItem(name:String,rect:BentoBoxRect,element:HtmlElement)

def BentoBox(size:BentoBoxSize, items:List[BentoBoxItem],mods:Modifier[Div]*):HtmlElement={
  val templateAreas = Array.fill(size.width,size.height) {
    "."
  }

  for {
    item <- items
    i <- item.rect.x until item.rect.x + item.rect.width
    j <- item.rect.y until item.rect.y + item.rect.height
  }{
    if !(i < 0 || i >= size.width || j < 0 || j >= size.height) then templateAreas(i)(j) = item.name
  }
  val areas = templateAreas.transpose.foldLeft("") { (acc, row) =>
    acc ++ s" \"${
      row.foldLeft("") { (acc, name) =>
        acc ++ s" ${name}"
      }
    }\""
  }

  div(
    display.grid,
    gridTemplateAreas := areas,
    mods,
    items.map { item =>
      div(
        gridArea := item.name,
        item.element
      )
    }
  )
}