package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

abstract class StatefulWidget extends Widget{
  var state:State=createState()
  var child:Widget=build()
  override def build():Widget = state.build()
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
  override def draw(renderer: WidgetRenderer): Unit = state.draw(renderer)
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs ${getClass.getSimpleName}")
    if(child!=null)child.debugPrint(s"$tabs\t")
  }

  override def findGestureDetectors(stack: mutable.Stack[GestureDetector], x: Float, y: Float): Unit = {
    if(child!=null&&child.contains(x,y))child.findGestureDetectors(stack,x,y)
  }
  def createState():State
}

abstract class State(w:StatefulWidget){
  var widget:StatefulWidget=w
  init()
  def build(): Widget
  def init():Unit={}
  def dispose():Unit={}
  def draw(renderer: WidgetRenderer): Unit = {
    if(widget.child!=null)widget.child.draw(renderer)
  }
  def setState(e:()=>Unit): Unit = {
    e()
//    widget.child
//    val newChild=build()
//    //uporedim oba stabla
//      //za svaki statefull widget
//        //ili napravi novi state i pozovi init
//        //ili dodeli stari state
//        //ili obrisi state i pozovi dispose
//    widget.child=newChild
  }
}
