package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import org.joml.Vector2f

class SizedBox(child:Widget,var width:Float,var height:Float) extends StatelessWidget {
  override def build(context:BuildContext): Widget = child

  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    size.set(
      Math.min(
        maxParentSize.x,
        width
      ),
      Math.min(
        maxParentSize.y,
        height
      )
    )
    if(child!=null){
      child.calculateSize(size)
    }
    size
  }
}

object SizedBox{
  def apply(child: Widget=null, width:Float=Float.MaxValue,height:Float=Float.MaxValue): SizedBox = new SizedBox(child, width,height)
}