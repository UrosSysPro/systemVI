package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

abstract class StatelessWidget extends Widget {
  var child:Widget=null
  override def build(context:BuildContext):Widget
  override def calculateSize(maxParentSize:Vector2f): Vector2f = {
    size.set(maxParentSize)
    if(child!=null){
      child.calculateSize(maxParentSize)
      size.set(child.size)
    }
    size
  }
  override def calculatePosition(parentPosition:Vector2f): Unit = {
    position.set(parentPosition)
    if(child!=null){
      child.calculatePosition(position)
    }
  }
  override def draw(context:DrawContext): Unit = {
    if(child!=null)child.draw(context)
  }
  override def getChildren(): Array[Widget] = Array(child)
}
