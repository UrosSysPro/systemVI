package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.Alignment.Alignment
import org.joml.Vector2f

class Align(child:Widget,val alignment:Alignment,
            val heightScale:Float= -1f,val widthScale:Float= -1f
           ) extends StatelessWidget {
  override def build(context: BuildContext): Widget = child
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    size.set(maxParentSize)
    if(child!=null){
      child.calculateSize(size)
      if(widthScale!= -1){
        size.x=child.size.x*widthScale
      }
      if(heightScale!= -1){
        size.y=child.size.y*heightScale
      }
    }
    size
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    
  }
}


