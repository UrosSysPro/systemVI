package com.systemvi.engine.ui

import com.systemvi.engine.window.Window
import org.joml.Vector2f

class Scene(val root:Widget,window:Window) {
  var width=window.getWidth
  var height=window.getHeight
  val renderer:WidgetRenderer=new WidgetRenderer(window)
  root.calculateSize(new Vector2f(width,height))
  root.calculatePosition(new Vector2f(0,0))

  def resize(): Unit = {

  }
  def draw():Unit={

  }
}