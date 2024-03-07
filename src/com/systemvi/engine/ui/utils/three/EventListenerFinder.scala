package com.systemvi.engine.ui.utils.three

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.widgets.GestureDetector
import org.joml.Vector2f

import scala.collection.mutable
import scala.collection.mutable.Stack
import scala.util.control.Breaks

class EventListenerFinder{
  val stack=new mutable.Stack[GestureDetector]()
  def find(widget: Widget):mutable.Stack[GestureDetector]={
    stack.clear()
    findR(widget)
    stack
  }
  private def findR(widget: Widget): Unit = widget match {
    case detector: GestureDetector=>
      stack.push(detector)
      for(child<-detector.getChildren())findR(child)
    case widget: Widget=>
      for(child<-widget.getChildren())findR(child)
    case null=>
  }
  def find(widget: Widget,mouse:Vector2f):mutable.Stack[GestureDetector]={
    stack.clear()
    findR(widget,mouse)
    stack
  }
  private def findR(widget: Widget,mouse:Vector2f): Unit = widget match {
    case detector: GestureDetector=>
      if(detector.contains(mouse.x,mouse.y)) {
        stack.push(detector)
        for (child <- detector.getChildren()) findR(child,mouse)
      }
    case widget: Widget=>
      if(widget.contains(mouse.x,mouse.y)){
        for(child<-widget.getChildren())findR(child,mouse)
      }
    case null=>
  }
}
