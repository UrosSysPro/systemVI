package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import org.joml.Vector2f

class Expanded(child:Widget,val flex:Float) extends StatelessWidget {
  override def build(context:BuildContext): Widget = child

  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    super.calculateSize(maxParentSize)
    size.set(maxParentSize)
    size
  }
}

object Expanded{
  def apply(child: Widget=null, flex:Float=1): Expanded = new Expanded(child, flex)
}