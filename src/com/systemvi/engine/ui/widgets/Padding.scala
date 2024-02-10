package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget

class Padding(var child:Widget,padding: EdgeInsets) extends Widget {
  override def build(): Unit = {

  }
}

object Padding{
  def apply(child: Widget,padding: EdgeInsets): Padding = new Padding(child,padding)
}

case class EdgeInsets(top:Float,bottom:Float,right:Float,left:Float)
object EdgeInsets{
  def all(value:Float):EdgeInsets=EdgeInsets(value,value,value,value)
  def only(top:Float=0,bottom:Float=0,right:Float=0,left:Float=0):EdgeInsets=
    EdgeInsets(top,bottom,right,left)
}
