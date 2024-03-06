package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import com.systemvi.engine.utils.ThreeBuilder
import org.joml.Vector2f

import scala.collection.mutable

abstract class StatefulWidget extends Widget{
  var state:State=null
  var child:Widget=null
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
  def createState():State

  override def getChildren(): Array[Widget] = Array(child)
}

abstract class State{
  var widget:StatefulWidget=null
  var threePosition:String=null
  var threeBuilder:ThreeBuilder=null
  def build(): Widget
  def init():Unit={}
  def dispose():Unit={}
  def draw(renderer: WidgetRenderer): Unit = {
    if(widget.child!=null)widget.child.draw(renderer)
  }
  def setState(e:()=>Unit): Unit = {
    e()
    threeBuilder.build(widget,threePosition)
  }
  def updateBeforeBuild(widget: StatefulWidget,threePosition:String,threeBuilder: ThreeBuilder): Unit = {
    this.widget=widget
    this.threePosition=threePosition
    this.threeBuilder=threeBuilder
  }
}
