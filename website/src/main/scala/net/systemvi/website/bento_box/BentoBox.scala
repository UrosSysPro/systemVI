package net.systemvi.website.bento_box

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}
import net.systemvi.website.CSSProps.*
import org.scalajs.dom

case class BentoBoxSize(width:Int,height:Int)
case class BentoBoxRect(x:Int,y:Int,width:Int,height:Int)
case class BentoBoxItem(name:String,rect:BentoBoxRect,element:Element)

def BentoBox(size:BentoBoxSize, items:List[BentoBoxItem],mods:Modifier[Div]*):HtmlElement={
  val templateAreas=Array.fill(size.width,size.height){"."}

  for{
    item<-items
    i<-item.rect.x until item.rect.x+item.rect.width
    j<-item.rect.y until item.rect.y+item.rect.height
  } templateAreas(i)(j)=item.name

  val areas=templateAreas.transpose.foldLeft(""){(acc,row)=>
    acc ++ s" \"${row.foldLeft(""){(acc,name)=>
      acc ++ s" ${name}"
    }}\""
  }
  dom.console.log(areas)
  div(
    display.grid,
    gridTemplateAreas:=areas,
    mods,
    items.map{item=>
      div(
        gridArea:=item.name,
        item.element
      )
    }
  )
}