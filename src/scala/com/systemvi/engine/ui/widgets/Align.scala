package com.systemvi.engine.ui.widgets
import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.utils.data.Alignment
import com.systemvi.engine.ui.utils.data.Alignment.Alignment
import org.joml.Vector2f

class Align(
             child:Widget,
             val alignment:Alignment,
             val heightScale:Float,
             val widthScale:Float
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
    if(child!=null){
      var x = parentPosition.x
      var y = parentPosition.y
      if(alignment==Alignment.topCenter||alignment==Alignment.center||alignment==Alignment.bottomCetner){
        x += size.x / 2 - child.size.x / 2
      }
      if(alignment==Alignment.topLeft||alignment==Alignment.centerLeft||alignment==Alignment.bottomLeft){
        x += size.x - child.size.x
      }
      if(alignment==Alignment.centerLeft||alignment==Alignment.center||alignment==Alignment.centerRight){
        y += size.y / 2 - child.size.y / 2
      }
      if(alignment==Alignment.bottomLeft||alignment==Alignment.bottomCetner||alignment==Alignment.bottomRight){
        y += size.y - child.size.y
      }
      position.set(x,y)
      child.calculatePosition(position)
    }
    position.set(parentPosition)
  }
}

object Align{
  def apply(child: Widget=null, alignment: Alignment=Alignment.center, heightScale: Float= -1, widthScale: Float= -1): Align = new Align(child, alignment, heightScale, widthScale)
}


