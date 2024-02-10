package com.systemvi.engine.ui

import org.joml.Vector2f

//  ui=build(state)

abstract class Widget {
  val position:Vector2f=new Vector2f()
  val size:Vector2f=new Vector2f()

  def build(): Unit
  def calculateSize(): Unit = {

  }
  def calculatePosition(): Unit = {

  }
  def draw(renderer:WidgetRenderer): Unit = {

  }
}
