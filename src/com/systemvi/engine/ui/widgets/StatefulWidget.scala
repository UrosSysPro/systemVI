package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.utils.tree.TreeBuilder
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

abstract class StatefulWidget extends Widget{
  var state:State=null
  var child:Widget=null
  override def build(context:BuildContext):Widget = state.build(context)
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
  override def draw(context:DrawContext): Unit = state.draw(context)
  def createState():State

  override def getChildren(): Array[Widget] = Array(child)
}

abstract class State{
  var markedForDeletion:Boolean=false;
  var widget:StatefulWidget=null
  var threePosition:String=null
  var threeBuilder:TreeBuilder=null
  var context:BuildContext=null
  def build(context: BuildContext): Widget
  def init():Unit={}
  def dispose():Unit={}
  def draw(context: DrawContext): Unit = {
    if(widget.child!=null)widget.child.draw(context)
  }
  def setState(e:()=>Unit): Unit = {
    e()
    threeBuilder.markForDeletion(widget,threePosition)
    threeBuilder.build(widget,threePosition,context)
    threeBuilder.deleteMarked(threePosition)
  }
  def updateBeforeBuild(widget: StatefulWidget, threePosition:String, threeBuilder: TreeBuilder, context: BuildContext): Unit = {
    this.widget=widget
    this.threePosition=threePosition
    this.threeBuilder=threeBuilder
    this.context=context
  }
}
