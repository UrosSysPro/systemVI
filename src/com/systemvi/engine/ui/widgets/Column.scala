package com.systemvi.engine.ui.widgets

import com.systemvi.engine.ui.utils.context.{BuildContext, DrawContext}
import com.systemvi.engine.ui.utils.data.AxisSize.AxisSize
import com.systemvi.engine.ui.utils.data.CrossAxisAlignment.CrossAxisAlignment
import com.systemvi.engine.ui.utils.data.{AxisSize, CrossAxisAlignment, MainAxisAlignment}
import com.systemvi.engine.ui.utils.data.MainAxisAlignment.MainAxisAlignment
import com.systemvi.engine.ui.{Widget, WidgetRenderer}
import org.joml.Vector2f

import scala.collection.mutable

class Column(
              val children:Array[Widget],
              val mainAxisAlignment: MainAxisAlignment,
              val crossAxisAlignment: CrossAxisAlignment,
              val mainAxisSize: AxisSize,
              val crossAxisSize:AxisSize
            ) extends StatelessWidget {
  override def build(context:BuildContext): Widget = null
  override def calculateSize(maxParentSize: Vector2f): Vector2f = {
    if(children==null){
      size.set(maxParentSize)
      return size
    }
    var totalFlex:Float=0
    var totalHeight:Float=0
    var nonFlexSize:Float=0
    var maxWidth:Float=0
    //izracunati velicinu za elemente koji nisu expanded
    for(child<-children){
      child match{
        case expanded: Expanded => totalFlex += expanded.flex
        case widget: Widget =>
          val childSize=widget.calculateSize(maxParentSize)
          nonFlexSize+=childSize.y
          totalHeight+=childSize.y
          if(childSize.x>maxWidth)maxWidth=childSize.x
        case _=>
      }
    }
    //izracunati velicinu za flex elemente
    val availableHeight=Math.max(0,maxParentSize.y-nonFlexSize)
    for(child<-children){
      child match{
        case expanded:Expanded=>
          val childSize=expanded.calculateSize(new Vector2f(maxParentSize.x,availableHeight*expanded.flex/totalFlex))
          if(childSize.x>maxWidth)maxWidth=childSize.x
          totalHeight+=childSize.y
        case _=>
      }
    }
    size.set(
      crossAxisSize match {
        case AxisSize.fit=>maxWidth
        case AxisSize.expand=>maxParentSize.x
      },
      mainAxisSize match {
        case AxisSize.fit=>totalHeight
        case AxisSize.expand=>maxParentSize.y
      }
    )
  }
  override def calculatePosition(parentPosition: Vector2f): Unit = {
    position.set(parentPosition)
    if(children==null)return
    var totalChildrenHeight:Float=0
    for(child<-children)if(child!=null)totalChildrenHeight+=child.size.y
    val freeSpace=size.y-totalChildrenHeight
    for((child,index)<-children.zipWithIndex){
      if(child!=null){
        val x: Float = crossAxisAlignment match {
          case CrossAxisAlignment.start => position.x
          case CrossAxisAlignment.end => position.x + size.x - child.size.x
          case CrossAxisAlignment.center => position.x + (size.x - child.size.x) / 2f
        }
        val y:Float=mainAxisAlignment match {
          case MainAxisAlignment.start=>
            var offsetFromStart: Float = 0f
            var i=0
            while(i<index){
              if(children(i)!=null)
                offsetFromStart+=children(i).size.y
              i+=1
            }
            position.y+offsetFromStart
          case MainAxisAlignment.end=>
            var offsetFromEnd: Float = 0f
            var i=children.length-1
            while(i>=index){
              if(children(i)!=null)
                offsetFromEnd+=children(i).size.y
              i-=1
            }
            position.y+size.y-offsetFromEnd
          case MainAxisAlignment.center=>
            var offsetFromStart: Float = 0f
            var i=0
            while(i<index) {
              if(children(i)!=null)
                offsetFromStart+=children(i).size.y
              i+=1
            }
            position.y+offsetFromStart+freeSpace/2
          case MainAxisAlignment.spaceAround=>
            var offsetFromStart: Float = 0f
            var i=0
            val spacing=freeSpace/(children.length+1)
            while(i<index) {
              if(children(i)!=null)
                offsetFromStart+=children(i).size.y+spacing
              i+=1
            }
            position.y+offsetFromStart+spacing
          case MainAxisAlignment.spaceBetween=>
            if(children.length>1){
              var offsetFromStart: Float = 0f
              var i=0
              val spacing=freeSpace/(children.length-1)
              while(i<index) {
                if(children(i)!=null)
                  offsetFromStart+=children(i).size.y+spacing
                i+=1
              }
              position.y+offsetFromStart
            }else{
              position.y+freeSpace/2
            }
        }
        child.calculatePosition(new Vector2f(x,y))
      }
    }
  }
  override def draw(context:DrawContext): Unit = {
    if(children==null)return
    for(child<-children){
      child.draw(context)
    }
  }
  override def getChildren(): Array[Widget] = children
}

object Column{
  def apply(
             children: Array[Widget]=Array(),
             mainAxisAlignment: MainAxisAlignment=MainAxisAlignment.start,
             crossAxisAlignment: CrossAxisAlignment=CrossAxisAlignment.center,
             mainAxisSize: AxisSize=AxisSize.expand,
             crossAxisSize:AxisSize=AxisSize.expand
           ): Column =
    new Column(children,mainAxisAlignment,crossAxisAlignment,mainAxisSize,crossAxisSize)
}
