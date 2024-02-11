package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import org.joml.Vector2f

class SizedBox(child:Widget,var preferedSize:Vector2f) extends StatelessWidget {
  override def build(): Widget = child

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

  override def debugPrint(tabs: String): Unit = {
    println(s"$tabs SizedBox size: ${size.x} ${size.y} position: ${position.x} ${position.y}")
    super.debugPrint(tabs)
  }
}

object SizedBox{
  def apply(child: Widget=null, size: Vector2f=new Vector2f(1000000,1000000)): SizedBox = new SizedBox(child, size)
}