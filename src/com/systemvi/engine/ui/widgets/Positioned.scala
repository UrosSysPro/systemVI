package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import org.joml.Vector2f

class Positioned(child:Widget,val top:Float,val left:Float,val right:Float,val bottom:Float) extends StatelessWidget {
  override def build(context:BuildContext): Widget = child
}

object Positioned{
  def apply(child: Widget,top:Float=0,left:Float=0,right:Float=0,bottom:Float=0): Positioned =
    new Positioned(child,top,left,right,bottom)
}
