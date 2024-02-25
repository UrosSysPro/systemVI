package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

abstract class StatefulWidget extends Widget{
  var state:State=null
  var child:Widget=build()
  override def build(): Widget = state.build()
  override def calculateSize(maxParentSize: Vector2f): Vector2f = ???
  override def calculatePosition(parentPosition: Vector2f): Unit = ???
  override def draw(renderer: WidgetRenderer): Unit = ???
  override def debugPrint(tabs: String): Unit = ???
  def createState():State
}

class State{
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
