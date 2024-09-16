package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.utils.data.CrossAxisAlignment.CrossAxisAlignment
import com.systemvi.engine.ui.utils.data.MainAxisAlignment.MainAxisAlignment
import com.systemvi.engine.ui.utils.data.{CrossAxisAlignment, MainAxisAlignment, AxisSize}
import com.systemvi.engine.ui.utils.data.AxisSize.AxisSize
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

class Row(
           val children:Array[Widget],
           val mainAxisAlignment: MainAxisAlignment,
           val crossAxisAlignment: CrossAxisAlignment,
           val mainAxisSize: AxisSize,
           val crossAxisSize:AxisSize
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
        case null=>
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
    size.set(
      mainAxisSize match {
        case AxisSize.fit=>totalWidth
        case AxisSize.expand=>maxParentSize.x
      },
      crossAxisSize match {
        case AxisSize.fit=>maxHeigth
        case AxisSize.expand=>maxParentSize.y
      }
    )
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(children==null)return
    var totalChildrenWidth:Float=0
    for(child<-children)if(child!=null)totalChildrenWidth+=child.size.x
    val freeSpace=size.x-totalChildrenWidth
    for((child,index)<-children.zipWithIndex){
      if(child!=null){
        val y: Float = crossAxisAlignment match {
          case CrossAxisAlignment.start => position.y
          case CrossAxisAlignment.end => position.y + size.y - child.size.y
          case CrossAxisAlignment.center => position.y + (size.y - child.size.y) / 2f
        }
        val x:Float=mainAxisAlignment match {
          case MainAxisAlignment.start=>
            var offsetFromStart: Float = 0f
            var i=0
            while(i<index){
              if(children(i)!=null)
                offsetFromStart+=children(i).size.x
              i+=1
            }
            position.x+offsetFromStart
          case MainAxisAlignment.end=>
            var offsetFromEnd: Float = 0f
            var i=children.length-1
            while(i>=index){
              if(children(i)!=null)
                offsetFromEnd+=children(i).size.x
              i-=1
            }
            position.x+size.x-offsetFromEnd
          case MainAxisAlignment.center=>
            var offsetFromStart: Float = 0f
            var i=0
            while(i<index) {
              if(children(i)!=null)
                offsetFromStart+=children(i).size.x
              i+=1
            }
            position.x+offsetFromStart+freeSpace/2
          case MainAxisAlignment.spaceAround=>
            var offsetFromStart: Float = 0f
            var i=0
            val spacing=freeSpace/(children.length+1)
            while(i<index) {
              if(children(i)!=null)
                offsetFromStart+=children(i).size.x+spacing
              i+=1
            }
            position.x+offsetFromStart+spacing
          case MainAxisAlignment.spaceBetween=>
            if(children.length>1){
              var offsetFromStart: Float = 0f
              var i=0
              val spacing=freeSpace/(children.length-1)
              while(i<index) {
                if(children(i)!=null)
                  offsetFromStart+=children(i).size.x+spacing
                i+=1
              }
              position.x+offsetFromStart
            }else{
              position.x+freeSpace/2
            }
        }
        child.calculatePosition(new Vector2f(x,y))
      }
    }
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
             children: Array[Widget]=Array(),
             mainAxisAlignment: MainAxisAlignment=MainAxisAlignment.start,
             crossAxisAlignment: CrossAxisAlignment=CrossAxisAlignment.center,
             mainAxisSize: AxisSize=AxisSize.expand,
             crossAxisSize:AxisSize=AxisSize.expand
           ): Row =
    new Row(children,mainAxisAlignment,crossAxisAlignment,mainAxisSize,crossAxisSize)
}
