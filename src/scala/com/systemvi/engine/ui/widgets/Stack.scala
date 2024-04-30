package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.{Widget}
import org.joml.Vector2f


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
          val left=if(positioned.left == -1)0 else positioned.left
          val right=if(positioned.right == -1)0 else positioned.right
          val top=if(positioned.top == -1)0 else positioned.top
          val bottom=if(positioned.bottom == -1) 0 else positioned.bottom
          val width=maxParentSize.x-right-left
          val height=maxParentSize.y-top-bottom
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
        case positioned: Positioned=>
          var x:Float=0
          var y:Float=0
          if(positioned.left != -1)x=positioned.left
          if(positioned.top != -1)y=positioned.top
          if(positioned.right!= -1)x=size.x-positioned.right-positioned.size.x
          if(positioned.bottom!= -1)y=size.y-positioned.bottom-positioned.size.y
          positioned.calculatePosition(new Vector2f(parentPosition).add(x,y))
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
