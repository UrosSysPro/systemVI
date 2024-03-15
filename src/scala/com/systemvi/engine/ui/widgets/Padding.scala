package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import org.joml.Vector2f

class Padding(child:Widget,var padding: EdgeInsets) extends StatelessWidget {
  override def build(context:BuildContext): Widget = child


  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    size.set(maxParentSize)
    if(child!=null){
      child.calculateSize(new Vector2f(size.x-padding.left-padding.right,size.y-padding.top-padding.bottom))
      size.set(child.size.x+padding.left+padding.right,child.size.y+padding.top+padding.bottom)
    }
    size
  }

  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(child!=null){
      child.calculatePosition(new Vector2f(parentPosition.x+padding.left,parentPosition.y+padding.top))
    }
  }
}

object Padding{
  def apply(child: Widget=null,padding: EdgeInsets=EdgeInsets.all(0)): Padding = new Padding(child,padding)
}

case class EdgeInsets(top:Float,bottom:Float,right:Float,left:Float)
object EdgeInsets{
  def all(value:Float):EdgeInsets=EdgeInsets(value,value,value,value)
  def only(top:Float=0,bottom:Float=0,right:Float=0,left:Float=0):EdgeInsets=
    EdgeInsets(top,bottom,right,left)
  def symetric(horizontal:Float,vertical:Float):EdgeInsets=EdgeInsets(vertical,vertical,horizontal,horizontal)
}
