package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import org.joml.Vector2f

class Positioned(child:Widget,val top:Float,val left:Float,val right:Float,val bottom:Float) extends StatelessWidget {
  override def build(): Widget = child
  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs Positioned size: ${size.x} ${size.y} position: ${position.x} ${position.y}")
    super.debugPrint(tabs)
  }
}

object Positioned{
  def apply(child: Widget,top:Float=0,left:Float=0,right:Float=0,bottom:Float=0): Positioned =
    new Positioned(child,top,left,right,bottom)
}
