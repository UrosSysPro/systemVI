package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

class Stack(val children:Array[Widget]) extends StatelessWidget {
  override def build(context:BuildContext): Widget = {
    null
  }
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    size.set(maxParentSize)
    if(children==null)return size
    for(child<-children){
      child match{
        case positioned:Positioned=>
          val width=maxParentSize.x-positioned.right-positioned.left
          val height=maxParentSize.y-positioned.top-positioned.bottom
          positioned.calculateSize(new Vector2f(width,height))
        case widget:Widget=>widget.calculateSize(maxParentSize)
        case _=>
      }
    }
    size
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(children==null)return
    for(child<-children){
      child match {
        case positioned: Positioned=>positioned.calculatePosition(new Vector2f(parentPosition).add(positioned.left,positioned.top))
        case widget: Widget=>widget.calculatePosition(parentPosition)
        case _=>
      }
    }
  }
  override def draw(context:DrawContext): Unit = {
    if(children==null)return
    for(child <-children){
      if(child!=null)child.draw(context)
    }
  }
  override def getChildren(): Array[Widget] = children
}

object Stack{
  def apply(children: Array[Widget]=Array()): Stack = new Stack(children)
}
