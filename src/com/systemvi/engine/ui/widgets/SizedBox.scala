package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import org.joml.Vector2f

class SizedBox(child:Widget,var preferedSize:Vector2f) extends StatelessWidget {
  override def build(context:BuildContext): Widget = child

  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    size.set(
      Math.min(
        maxParentSize.x,
        preferedSize.x
      ),
      Math.min(
        maxParentSize.y,
        preferedSize.y
      )
    )
    if(child!=null){
      child.calculateSize(size)
    }
    return size
  }
}

object SizedBox{
  def apply(child: Widget=null, size: Vector2f=new Vector2f(Float.MaxValue,Float.MaxValue)): SizedBox = new SizedBox(child, size)
}