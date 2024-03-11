package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.utils.data.CrossAxisAlignment.CrossAxisAlignment
import com.systemvi.engine.ui.utils.data.MainAxisAlignment.MainAxisAlignment
import com.systemvi.engine.ui.utils.data.{CrossAxisAlignment, MainAxisAlignment, MainAxisSize}
import com.systemvi.engine.ui.utils.data.MainAxisSize.MainAxisSize
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

class Row(
           val children:Array[Widget],
           val mainAxisAlignment: MainAxisAlignment,
           val crossAxisAlignment: CrossAxisAlignment,
           val mainAxisSize: MainAxisSize
         ) extends StatelessWidget {
  override def build(context:BuildContext): Widget = {
    null
  }
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    if(children==null){
      size.set(maxParentSize)
      return size
    }
    var totalFlex:Float=0
    var totalWidth:Float=0
    var nonFlexSize:Float=0
    var maxHeigth:Float=0
    //izracunati velicinu za elemente koji nisu expanded
    for(child<-children){
      child match{
        case expanded: Expanded => totalFlex += expanded.flex
        case widget: Widget =>
          val childSize=widget.calculateSize(maxParentSize)
          nonFlexSize+=childSize.x
          totalWidth+=childSize.x
          if(childSize.y>maxHeigth)maxHeigth=childSize.y
        case _=>
      }
    }
    //izracunati velicinu za flex elemente
    val availableWidth=Math.max(0,maxParentSize.x-nonFlexSize)
    for(child<-children){
      child match{
        case expanded:Expanded=>
          val childSize=expanded.calculateSize(new Vector2f(availableWidth*expanded.flex/totalFlex,maxParentSize.y))
          if(childSize.y>maxHeigth)maxHeigth=childSize.y
          totalWidth+=childSize.x
        case _=>
      }
    }
    size.set(totalWidth,maxHeigth)
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(children==null)return
//    val currentPosition=new Vector2f(parentPosition)
//    for(child<-children){
//      if(child!=null){
//        child.calculatePosition(currentPosition)
//        currentPosition.x+=child.size.x
//      }
//    }
    var totalChildrenWidth=0
    for(child<-children)if(child!=null)totalChildrenWidth+=child.size.x
  }
  override def draw(context:DrawContext): Unit = {
    if (children == null) return
    for (child <- children) {
      if (child != null) child.draw(context)
    }
  }
  override def getChildren(): Array[Widget] = children
}

object Row{
  def apply(
             children: Array[Widget]=null,
             mainAxisSize: MainAxisSize=MainAxisSize.expand,
             crossAxisAlignment: CrossAxisAlignment=CrossAxisAlignment.center,
             mainAxisAlignment: MainAxisAlignment=MainAxisAlignment.start
           ): Row =
    new Row(children,mainAxisAlignment,crossAxisAlignment,mainAxisSize)
}
