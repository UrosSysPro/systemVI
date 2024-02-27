package com.systemvi.engine.ui

import com.systemvi.engine.utils.Utils
import com.systemvi.engine.window.Window
import org.joml.Vector2f

class Scene(val root:Widget,window:Window) {
  var width=window.getWidth
  var height=window.getHeight
  val renderer:WidgetRenderer=new WidgetRenderer(window)
  resize(window.getWidth,window.getHeight)
  def resize(width:Int,height:Int): Unit = {
    this.width=width
    this.height=height
    root.calculateSize(new Vector2f(width,height))
    root.calculatePosition(new Vector2f(0,0))
    renderer.camera.setScreenSize(width,height)
    renderer.camera.update()
  }
  def draw():Unit={
    Utils.enableBlending()
    root.draw(renderer)
    renderer.flush()
    Utils.disableBlending()
  }
}

object Scene{
  def apply(root: Widget, window: Window): Scene = new Scene(root, window)
}