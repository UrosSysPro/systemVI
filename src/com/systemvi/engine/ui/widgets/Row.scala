package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

class Row(val children:Array[Widget]) extends StatelessWidget {
  override def build(): Widget = {
    null
  }
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    if(children==null){
      size.set(maxParentSize)
      return size
    }
    var totalFlex:Float=0
    var totalWidth:Float=0
    var nonFlexSize:Float=0
    var maxHeigth:Float=0
    //izracunati velicinu za elemente koji nisu expanded
    for(child<-children){
      child match{
        case expanded: Expanded => totalFlex += expanded.flex
        case widget: Widget =>
          val childSize=widget.calculateSize(maxParentSize)
          nonFlexSize+=childSize.x
          totalWidth+=childSize.x
          if(childSize.y>maxHeigth)maxHeigth=childSize.y
        case _=>
      }
    }
    //izracunati velicinu za flex elemente
    val availableWidth=Math.max(0,maxParentSize.x-nonFlexSize)
    for(child<-children){
      child match{
        case expanded:Expanded=>
          val childSize=expanded.calculateSize(new Vector2f(availableWidth*expanded.flex/totalFlex,maxParentSize.y))
          if(childSize.y>maxHeigth)maxHeigth=childSize.y
          totalWidth+=childSize.x
        case _=>
      }
    }
    size.set(totalWidth,maxHeigth)
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(children==null)return
    val currentPosition=new Vector2f(parentPosition)
    for(child<-children){
      child.calculatePosition(currentPosition)
      currentPosition.x+=child.size.x
    }
  }
  override def draw(renderer: WidgetRenderer): Unit = {
    if(children==null)return
    for(child<-children){
      child.draw(renderer)
    }
  }
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs Row size: ${size.x} ${size.y} position: ${position.x} ${position.y}")
    if(children==null)return
    val childTabs=s"$tabs\t"
    for(child<-children){
      child.debugPrint(childTabs)
    }
  }
  override def findGestureDetectors(stack: mutable.Stack[GestureDetector], x: Float, y: Float): Unit = {
    if(children!=null)for(child<-children){
      if(child.contains(x,y))child.findGestureDetectors(stack, x, y)
    }
  }
  override def getChildren(): Array[Widget] = children
}

object Row{
  def apply(children: Array[Widget]=null): Row = new Row(children)
}
