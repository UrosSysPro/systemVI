package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

class Column(val children:Array[Widget]) extends StatelessWidget {
  override def build(): Widget = null
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    if(children==null){
      size.set(maxParentSize)
      return size
    }
    var totalFlex:Float=0
    var totalHeight:Float=0
    var nonFlexSize:Float=0
    var maxWidth:Float=0
    //izracunati velicinu za elemente koji nisu expanded
    for(child<-children){
      child match{
        case expanded: Expanded => totalFlex += expanded.flex
        case widget: Widget =>
          val childSize=widget.calculateSize(maxParentSize)
          nonFlexSize+=childSize.y
          totalHeight+=childSize.y
          if(childSize.x>maxWidth)maxWidth=childSize.x
        case _=>
      }
    }
    //izracunati velicinu za flex elemente
    val availableHeight=Math.max(0,maxParentSize.y-nonFlexSize)
    for(child<-children){
      child match{
        case expanded:Expanded=>
          val childSize=expanded.calculateSize(new Vector2f(maxParentSize.x,availableHeight*expanded.flex/totalFlex))
          if(childSize.x>maxWidth)maxWidth=childSize.x
          totalHeight+=childSize.y
        case _=>
      }
    }
    size.set(maxWidth,totalHeight)
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(children==null)return
    val currentPosition=new Vector2f(parentPosition)
    for(child<-children){
      child.calculatePosition(currentPosition)
      currentPosition.y+=child.size.y
    }
  }
  override def draw(renderer: WidgetRenderer): Unit = {
    if(children==null)return
    for(child<-children){
      child.draw(renderer)
    }
  }
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs Column size: ${size.x} ${size.y} position: ${position.x} ${position.y}")
    if(children==null)return
    val childTabs=s"$tabs\t"
    for(child<-children){
      child.debugPrint(childTabs)
    }
  }
}

object Column{
  def apply(children: Array[Widget]=null): Column = new Column(children)
}
