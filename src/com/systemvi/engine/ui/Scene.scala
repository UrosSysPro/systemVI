package com.systemvi.engine.ui

import com.systemvi.engine.ui.widgets.GestureDetector
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.window.{InputProcessor, Window}
import org.joml.Vector2f

import scala.collection.mutable

class Scene(val root:Widget,window:Window) extends InputProcessor{
  var width: Int =window.getWidth
  var height: Int =window.getHeight
  val renderer:WidgetRenderer=new WidgetRenderer(window)
  var focused:GestureDetector=null
  resize(window.getWidth,window.getHeight)
  def resize(width:Int,height:Int): Boolean = {
    this.width=width
    this.height=height
    root.calculateSize(new Vector2f(width,height))
    root.calculatePosition(new Vector2f(0,0))
    renderer.camera.setScreenSize(width,height)
    renderer.camera.update()
    true
  }
  def draw():Unit={
    Utils.enableBlending()
    root.draw(renderer)
    renderer.flush()
    Utils.disableBlending()
  }

  override def keyDown(key: Int, scancode: Int, mods: Int): Boolean = {

    false
  }
  override def keyUp(key: Int, scancode: Int, mods: Int): Boolean = {
    false
  }
  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    val stack:mutable.Stack[GestureDetector]=new mutable.Stack[GestureDetector]();
    root.findGestureDetectors(stack,x.toFloat,y.toFloat)
    stack.foreach(detector=>{
      print(detector)
    })

    var detected=false
    while(stack.nonEmpty && !detected){
      val detector=stack.pop()
      if(detector.mouseDown(button,mods,x,y)){
        if(detector.focusable)focused=detector else focused=null
        detected=true
      }
    }
    false
  }
  override def mouseUp(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    false
  }
  override def mouseMove(x: Double, y: Double): Boolean = {
    false
  }
  override def scroll(offsetX: Double, offsetY: Double): Boolean = {
    false
  }
}

object Scene{
  def apply(root: Widget, window: Window): Scene = {
    new Scene(root, window)
  }
}