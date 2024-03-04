package com.systemvi.engine.ui

import com.systemvi.engine.ui.widgets.GestureDetector
import org.joml.Vector2f

import scala.collection.mutable

//  ui=build(state)

abstract class Widget {
  val position:Vector2f=new Vector2f()
  val size:Vector2f=new Vector2f()

  def build(): Widget
  def calculateSize(maxParentSize: Vector2f): Vector2f
  def calculatePosition(parentPosition:Vector2f): Unit
  def draw(renderer:WidgetRenderer): Unit
  def debugPrint(tabs:String): Unit
  def contains(x:Float,y:Float):Boolean=
    x>=position.x&&
    x<=position.x+size.x&&
    y>=position.y&&
    y<=position.y+size.y
  def getChildren():Array[Widget]
}