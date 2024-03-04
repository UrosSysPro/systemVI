package com.systemvi.engine.ui

import com.systemvi.engine.ui.utils.EventListenerFinder
import com.systemvi.engine.ui.widgets.GestureDetector
import com.systemvi.engine.utils.Utils
import com.systemvi.engine.window.{InputProcessor, Window}
import org.joml.Vector2f

class Scene(val root:Widget,window:Window) extends InputProcessor{
  var width: Int =window.getWidth
  var height: Int =window.getHeight
  val renderer:WidgetRenderer=new WidgetRenderer(window)
  var focused:GestureDetector=null
  val mouse=new Vector2f()
  val eventListenerFinder=new EventListenerFinder()
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
    if(focused!=null)return focused.keyDown(key,scancode,mods)
    false
  }
  override def keyUp(key: Int, scancode: Int, mods: Int): Boolean = {
    if(focused!=null)return focused.keyUp(key,scancode,mods)
    false
  }
  override def mouseDown(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    mouse.set(x,y)
    val stack=eventListenerFinder.find(root,mouse)
    while(stack.nonEmpty){
      val detector=stack.pop()
      if(detector.mouseDown(button,mods,x,y)){
        if(detector.focusable)focused=detector else focused=null
      }
    }
    false
  }
  override def mouseUp(button: Int, mods: Int, x: Double, y: Double): Boolean = {
    if(focused!=null)return focused.mouseUp(button,mods,x,y)
    false
  }
  override def mouseMove(x: Double, y: Double): Boolean = {
    mouse.set(x,y)
    //mouse enter
    var stack=eventListenerFinder.find(root)
    while (stack.nonEmpty){
      val widget=stack.pop()
      if(!widget.mouseOver&&widget.contains(mouse.x,mouse.y)){
        widget.mouseOver=true
        widget.mouseEnter()
      }
    }
    //mouse leave
    stack=eventListenerFinder.find(root)
    while (stack.nonEmpty){
      val widget=stack.pop()
      if(widget.mouseOver&& !widget.contains(mouse.x,mouse.y)){
        widget.mouseOver=false
        widget.mouseLeave()
      }
    }
    //mouse move
    stack=eventListenerFinder.find(root,mouse)
    while(stack.nonEmpty){
      val detector=stack.pop()
      if(detector.mouseMove(x,y)){
        if(detector.focusable)focused=detector else focused=null
      }
    }
    false
  }
  override def scroll(offsetX: Double, offsetY: Double): Boolean = {
    val stack=eventListenerFinder.find(root,mouse)
    while(stack.nonEmpty){
      val detector=stack.pop()
      if(detector.scroll(offsetX,offsetY)){
        if(detector.focusable)focused=detector else focused=null
      }
    }
    false
  }
}

object Scene{
  def apply(root: Widget, window: Window): Scene = {
    new Scene(root, window)
  }
}