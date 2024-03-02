package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

abstract class StatelessWidget extends Widget {
  var child:Widget=build()
  override def build():Widget
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
  override def draw(renderer: WidgetRenderer): Unit = {
    if(child!=null)child.draw(renderer)
  }
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs ${getClass.getSimpleName}")
    if(child!=null)child.debugPrint(s"$tabs\t")
  }

  override def findGestureDetectors(stack: mutable.Stack[GestureDetector], x: Float, y: Float): Unit = {
    this match {
      case detector: GestureDetector=>stack.push(detector)
      case _=>
    }
    if(child!=null)child.findGestureDetectors(stack,x,y)
  }
}
