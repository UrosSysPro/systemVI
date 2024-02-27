package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

abstract class StatefulWidget extends Widget{
  var state:State=null
  var child:Widget=build()
  override def build():Widget = state.build()
  override def calculateSize(maxParentSize:Vector2f): Vector2f = {
    size.set(maxParentSize)
    if(child!=null){
      child.calculateSize(maxParentSize)
      size.set(child.size)
    }
    return size
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
    println(s"$tabs StatefulWidget")
    if(child!=null)child.debugPrint(s"$tabs\t")
  }
  def createState():State
}

abstract class State{
  var widget:StatefulWidget=null;
  def build(): Widget = null

  def init():Unit=Unit
  def dispose():Unit=Unit
  def setState(e:()=>Unit): Unit = {
    widget.child
    val newChild=build()
    //uporedim oba stabla
      //za svaki statefull widget
        //ili napravi novi state i pozovi init
        //ili dodeli stari state
        //ili obrisi state i pozovi dispose
    widget.child=newChild
  }
}
