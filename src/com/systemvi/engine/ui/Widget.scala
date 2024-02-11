package com.systemvi.engine.ui

import org.joml.Vector2f

//  ui=build(state)

abstract class Widget {
  val position:Vector2f=new Vector2f()
  val size:Vector2f=new Vector2f()

  def build(): Widget
  def calculateSize(maxParentSize: Vector2f): Vector2f
  def calculatePosition(parentPosition:Vector2f): Unit
  def draw(renderer:WidgetRenderer): Unit
  def debugPrint(tabs:String): Unit
}