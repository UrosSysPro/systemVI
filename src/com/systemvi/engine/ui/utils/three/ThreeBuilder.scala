package com.systemvi.engine.ui.utils.three

import com.systemvi.engine.ui.Widget
import com.systemvi.engine.ui.utils.context.BuildContext
import com.systemvi.engine.ui.widgets.{State, StatefulWidget, StatelessWidget}

class ThreeBuilder(var states:Map[String,State]) {
  private def getState(widget:StatefulWidget,threePosition:String):State=
    states.get(threePosition) match {
      case Some(state)=>
        state.markedForDeletion=false
        state
      case None=>
        val state=widget.createState()
        state.init()
        state.markedForDeletion=false
        states=states+(threePosition->state)
        state
    }
  def build(root:Widget,threePosition:String,context:BuildContext): Unit = root match {
    case null=>

    case widget:StatelessWidget=>
      widget.child=widget.build(context)
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>build(child,s"$threePosition/${child.getClass.getSimpleName}.$index",context)
        case (null,_:Int)=>
      })
    case widget:StatefulWidget=>
      widget.state=getState(widget,threePosition)
      widget.state.updateBeforeBuild(widget,threePosition,this,context)

      widget.child=widget.build(context)
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>build(child,s"$threePosition/${child.getClass.getSimpleName}.$index",context)
        case (null,_:Int)=>
      })
    case widget: Widget=>println(s"[BUILD ERROR] ${widget.getClass.getSimpleName} not a stateful or stateless widget")
  }
  def markForDeletion(root:Widget,threePosition:String):Unit= root match {
    case null=>

    case widget:StatelessWidget=>
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>markForDeletion(child,s"$threePosition/${child.getClass.getSimpleName}.$index")
        case (null,_:Int)=>
      })
    case widget:StatefulWidget=>
      widget.state.markedForDeletion=true
      widget.getChildren().zipWithIndex.foreach({
        case (child: Widget,index:Int)=>markForDeletion(child,s"$threePosition/${child.getClass.getSimpleName}.$index")
        case (null,_:Int)=>
      })
    case widget: Widget=>println(s"[BUILD ERROR] ${widget.getClass.getSimpleName} not a stateful or stateless widget")
  }
  def deleteMarked(threePosition:String):Unit={
    val keys=states.keys
    for(key<-keys){
      if(key.indexOf(threePosition)==0)states.get(key) match {
        case Some(state)=>if(state.markedForDeletion){
          state.dispose()
          states=states-key
        }
        case None=>
      }
    }
  }
}
