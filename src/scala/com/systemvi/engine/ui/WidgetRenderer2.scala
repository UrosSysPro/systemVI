package com.systemvi.engine.ui

import org.joml.{Matrix4f, Vector4f}

case class Rect(x:Float,y:Float,width:Float,height:Float,rotation:Float)
case class Border(radius:Float,width:Float,color:Vector4f)
case class Drawable(rect:Rect,color:Vector4f,border:Border,blur:Float,boundry:Rect,glyph:Rect,transform:Matrix4f){
  def writeToArray(array:Array[Float]): Unit = {

  }
}

class WidgetRenderer2 {

}
